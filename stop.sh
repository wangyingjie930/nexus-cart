#!/bin/bash

# Cart Service 停止脚本
# 描述: 停止 nexus-cart 微服务

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 获取脚本所在目录的绝对路径
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 服务配置
SERVICE_NAME="cart-service"
PID_FILE="$SCRIPT_DIR/${SERVICE_NAME}.pid"

echo -e "${BLUE}🛑 开始停止 $SERVICE_NAME...${NC}"

# 检查PID文件是否存在
if [ ! -f "$PID_FILE" ]; then
    echo -e "${YELLOW}⚠️  没有找到PID文件 ($PID_FILE)。${NC}"
    echo -e "${YELLOW}   尝试通过JAR文件名查找进程...${NC}"
    # pom.xml中定义了artifactId为'cart'
    pkill -f "cart-0.0.1-SNAPSHOT.jar"
    if [ $? -eq 0 ]; then
       echo -e "${GREEN}✅ 已尝试停止相关Java进程。${NC}"
    else
       echo -e "${RED}❌ 未找到正在运行的服务进程。${NC}"
    fi
    exit 0
fi

# 读取PID并停止进程
pid=$(cat "$PID_FILE")
if [ -z "$pid" ]; then
    echo -e "${RED}❌ PID文件为空。${NC}"
    exit 1
fi

if ! kill -0 "$pid" 2>/dev/null; then
    echo -e "${YELLOW}⚠️  PID为 $pid 的进程不存在或已停止。${NC}"
    rm -f "$PID_FILE"
    exit 0
fi

# 尝试优雅地停止
echo -e "${BLUE}🔧 尝试优雅地停止进程 (PID: $pid)...${NC}"
kill -TERM "$pid"

# 等待最多15秒
for i in {1..15}; do
    if ! kill -0 "$pid" 2>/dev/null; then
        echo -e "${GREEN}✅ 进程已成功停止 (PID: $pid)。${NC}"
        rm -f "$PID_FILE"
        exit 0
    fi
    sleep 1
done

# 如果还未停止，则强制杀死
echo -e "${YELLOW}⚠️  进程未能优雅停止，将强制杀死 (PID: $pid)...${NC}"
kill -9 "$pid"
sleep 1

if kill -0 "$pid" 2>/dev/null; then
    echo -e "${RED}❌ 无法停止进程 (PID: $pid)。请手动检查。${NC}"
    exit 1
else
    echo -e "${GREEN}✅ 进程已强制停止。${NC}"
    rm -f "$PID_FILE"
fi

echo -e "${GREEN}🎉 $SERVICE_NAME 已完全停止！${NC}"