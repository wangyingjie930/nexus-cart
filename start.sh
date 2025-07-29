#!/bin/bash

# Cart Service 启动脚本
# 描述: 启动 nexus-cart 微服务

set -e  # 遇到错误时退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 获取脚本所在目录的绝对路径
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 服务配置 (从 pom.xml 和 application.yml 中获取)
SERVICE_NAME="cart-service"
SERVICE_PORT="8088" #
JAR_PATH="$SCRIPT_DIR/target/cart-0.0.1-SNAPSHOT.jar" #
LOG_DIR="$SCRIPT_DIR/logs"
PID_FILE="$SCRIPT_DIR/${SERVICE_NAME}.pid"

# 设置环境变量 (参考 application.yml 和 promotion 服务的 start.sh)
# 这些值可以在启动时被外部环境变量覆盖
export REDIS_ADDR="${REDIS_ADDR:-redis.infra}" #
export REDIS_PORT="${REDIS_PORT:-6379}" #
export PROMOTION_SERVICE_URL="${PROMOTION_SERVICE_URL:-http://promotion-service.default.svc.cluster.local:8087}" #

# 创建必要的目录
mkdir -p "$LOG_DIR"

echo -e "${BLUE}🚀 开始启动 $SERVICE_NAME...${NC}"

# 检查端口是否被占用
if lsof -Pi :$SERVICE_PORT -sTCP:LISTEN -t >/dev/null ; then
    echo -e "${YELLOW}⚠️  端口 $SERVICE_PORT 已被占用，尝试停止现有服务...${NC}"
    pid=$(lsof -Pi :$SERVICE_PORT -sTCP:LISTEN -t)
    kill -9 $pid
    echo -e "${GREEN}✅ 已停止占用端口的进程 (PID: $pid)${NC}"
    sleep 2
fi

# 清理旧的PID文件
if [ -f "$PID_FILE" ]; then
    echo -e "${YELLOW}🔧 发现旧的PID文件，正在清理...${NC}"
    rm -f "$PID_FILE"
fi


# 编译和打包Java项目
echo -e "${BLUE}🔧 使用 Maven 打包项目 (./mvnw clean package)...${NC}"
# 确保 mvnw 脚本有执行权限
chmod +x "$SCRIPT_DIR/mvnw"
# 执行打包，跳过测试以加快启动速度
"$SCRIPT_DIR/mvnw" clean package -DskipTests

if [ ! -f "$JAR_PATH" ]; then
    echo -e "${RED}❌ 打包失败: 未在 $JAR_PATH 找到JAR文件${NC}"
    exit 1
fi
echo -e "${GREEN}✅ 打包成功${NC}"


# 启动服务
echo -e "${BLUE}🔧 启动 $SERVICE_NAME (端口: $SERVICE_PORT)...${NC}"
# 使用 nohup 在后台运行，并将日志重定向
nohup java -jar "$JAR_PATH" > "$LOG_DIR/$SERVICE_NAME.log" 2>&1 &
SERVICE_PID=$!
echo $SERVICE_PID > "$PID_FILE"

# 等待服务启动
echo -e "${YELLOW}⏳ 等待服务启动...${NC}"
sleep 5 # Spring Boot启动可能需要更长时间

# 检查服务是否成功启动
if kill -0 $SERVICE_PID 2>/dev/null; then
    echo -e "${GREEN}✅ $SERVICE_NAME 已成功启动 (PID: $SERVICE_PID)${NC}"
    echo -e "${BLUE}📋 服务信息:${NC}"
    echo -e "  - 服务名称: $SERVICE_NAME"
    echo -e "  - 服务端口: $SERVICE_PORT"
    echo -e "  - 进程ID:   $SERVICE_PID"
    echo -e "  - 日志文件: $LOG_DIR/$SERVICE_NAME.log"
    echo ""
    echo -e "${YELLOW}💡 API 测试示例:${NC}"
    echo -e "  curl http://localhost:$SERVICE_PORT/api/v1/carts/1"
else
    echo -e "${RED}❌ $SERVICE_NAME 启动失败${NC}"
    echo -e "${YELLOW}📋 查看日志获取详细信息: tail -f $LOG_DIR/$SERVICE_NAME.log${NC}"
    exit 1
fi

echo -e "${GREEN}🎉 $SERVICE_NAME 启动完成！${NC}"
echo -e "${BLUE}🛑 停止服务请运行: ./stop.sh${NC}"