package com.alibaba.nexus.cart.interfaces;

import com.alibaba.nexus.cart.application.CartService;
import com.alibaba.nexus.cart.application.dto.CartView;
import com.alibaba.nexus.cart.application.dto.UserContext;
import com.alibaba.nexus.cart.domain.CartItem;
import com.alibaba.nexus.cart.infrastructure.client.PromotionServiceClient;
import com.alibaba.nexus.cart.infrastructure.client.dto.DiscountApplication;
import com.alibaba.nexus.cart.interfaces.dto.AddItemRequest;
import com.alibaba.nexus.cart.interfaces.dto.UpdateQuantityRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PromotionServiceClient promotionServiceClient;

    @Autowired
    private CartService cartService;

    private final String userId = "testUser";
    private final String sku = "testSku";

    @BeforeEach
    void setUp() {
        // 在每个测试开始前，清空该用户的购物车，避免测试间互相影响
        cartService.clearCart(userId);

        // 设定一个通用的模拟返回：默认没有任何优惠
        DiscountApplication noDiscount = new DiscountApplication(0L, "NO_DISCOUNT", "无优惠");
        when(promotionServiceClient.calculateBestOffer(any())).thenReturn(noDiscount);
    }

    @Test
    void testGetCart_WhenCartIsEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/carts/{userId}", userId)
                        .header("X-User-Vip", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.totalAmount").value(0));
    }

    @Test
    void testAddItemToCart() throws Exception {
        AddItemRequest request = new AddItemRequest();
        request.setSku(sku);
        request.setPrice(1000L); // 10.00元
        request.setQuantity(2);
        request.setCategory("testCategory");
        request.setBrand("testBrand");

        mockMvc.perform(post("/api/v1/carts/{userId}/items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.items[0].sku").value(sku))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].totalPrice").value(2000))
                .andExpect(jsonPath("$.totalAmount").value(2000));
    }

    @Test
    void testAddItemToCart_WithPromotion() throws Exception {
        // 模拟促销服务返回一个10元的优惠
        DiscountApplication discount = new DiscountApplication(1000L, "TEN_YUAN_OFF", "满1件减10元");
        when(promotionServiceClient.calculateBestOffer(any())).thenReturn(discount);

        AddItemRequest request = new AddItemRequest();
        request.setSku(sku);
        request.setPrice(5000L); // 50.00元
        request.setQuantity(1);
        request.setCategory("testCategory");
        request.setBrand("testBrand");

        mockMvc.perform(post("/api/v1/carts/{userId}/items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(5000))
                .andExpect(jsonPath("$.discountAmount").value(1000))
                .andExpect(jsonPath("$.finalAmount").value(4000)) // 50 - 10 = 40
                .andExpect(jsonPath("$.discountDescription").value("满1件减10元"));
    }


    @Test
    void testUpdateItemQuantity() throws Exception {
        // 先添加一个商品
        testAddItemToCart();

        UpdateQuantityRequest updateRequest = new UpdateQuantityRequest();
        updateRequest.setQuantity(5);

        mockMvc.perform(put("/api/v1/carts/{userId}/items/{sku}", userId, sku)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].sku").value(sku))
                .andExpect(jsonPath("$.items[0].quantity").value(5))
                .andExpect(jsonPath("$.items[0].totalPrice").value(5000)) // 1000 * 5
                .andExpect(jsonPath("$.totalAmount").value(5000));
    }

    @Test
    void testUpdateItemQuantityToZero_ShouldRemoveItem() throws Exception {
        // 先添加一个商品
        testAddItemToCart();

        UpdateQuantityRequest updateRequest = new UpdateQuantityRequest();
        updateRequest.setQuantity(0);

        mockMvc.perform(put("/api/v1/carts/{userId}/items/{sku}", userId, sku)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void testRemoveItemFromCart() throws Exception {
        // 先添加一个商品
        testAddItemToCart();

        // 再添加另一个商品
        AddItemRequest request2 = new AddItemRequest();
        request2.setSku("sku-another");
        request2.setPrice(3000L);
        request2.setQuantity(1);
        request2.setCategory("cat2");
        request2.setBrand("brand2");
        mockMvc.perform(post("/api/v1/carts/{userId}/items", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));

        // 现在购物车里应该有两个商品，我们删除第一个
        mockMvc.perform(delete("/api/v1/carts/{userId}/items/{sku}", userId, sku))
                .andExpect(status().isNoContent());

        // 验证购物车里是否只剩下第二个商品
        mockMvc.perform(get("/api/v1/carts/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].sku").value("sku-another"))
                .andExpect(jsonPath("$.totalAmount").value(3000));
    }
}