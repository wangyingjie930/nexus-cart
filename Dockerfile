# ---- Stage 1: Builder ----
# 使用官方的 Maven 镜像，它包含了 JDK 17，与 pom.xml 中定义的 Java 版本一致
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# 设置工作目录
WORKDIR /app

# 缓存依赖
# 仅复制 pom.xml，以便在依赖没有变化时利用 Docker 的层缓存
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制整个项目源代码
COPY src ./src
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn ./.mvn

# 打包应用，生成可执行的 JAR 文件
# -DskipTests 会跳过测试，加快构建速度
RUN mvn package -DskipTests


# ---- Stage 2: Runner ----
# 使用一个非常精简的 JRE 镜像，以减小最终镜像的体积
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 从 builder 阶段复制构建好的 JAR 文件
# 根据 pom.xml 中的 artifactId 和 version
COPY --from=builder /app/target/cart-0.0.1-SNAPSHOT.jar /app/app.jar

# 暴露 application.yml 中定义的服务端口
EXPOSE 8088

# 容器启动时执行的命令
# 使用 exec 格式以使 Java 应用成为容器的 PID 1 进程
CMD ["java", "-jar", "/app/app.jar"]