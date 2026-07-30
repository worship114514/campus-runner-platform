package com.runner.utils;
/**
 * 通用数据脱敏工具类
 * 脱敏：隐藏数据中的敏感部分，只显示前后少量字符，中间用*替代（保护用户隐私）
 * 适用场景：
 *
 1. 用户名（如：民大新闻中心 → 民******心）
 *
 *
 *
 2. 手机号（如：13900000000 → 1******0）
 3. 邮箱（如：admin@gzmu.edu.cn → a******n）
 4. 地址（如：花溪大学城 → 花******城）
 * 核心设计：根据字符串长度动态调整脱敏规则，兼顾不同长度数据的脱敏效果
 */
public class DesensitizationUtil {
    /**
     * 固定脱敏长度（当字符串较长时，中间固定显示6个*）
     */
    private static final int SIZE = 6;
    /**
     * 脱敏替换符（统一用*隐藏敏感内容）
     */
    private static final String SYMBOL = "*";
    /**
     * 测试方法：验证不同类型数据的脱敏效果
     * 可直接运行main方法，查看控制台输出的脱敏结果
     */
    public static void main(String[] args) {
        String name = commonDisplay("民大新闻中心");   // 测试用户名脱敏
        String mobile = commonDisplay("13900000000"); // 测试手机号脱敏
        String mail = commonDisplay("admin@gzmu.edu.cn"); // 测试邮箱脱敏
        String address = commonDisplay("花溪大学城");   // 测试地址脱敏
        // 打印脱敏结果，验证规则是否生效
        System.out.println(name);    // 输出：民******心
        System.out.println(mobile);  // 输出：1******0
        System.out.println(mail);    // 输出：a******n
        System.out.println(address); // 输出：花******城
    }
    /**
     * 核心方法：通用脱敏逻辑（适配不同长度的字符串）
     * 脱敏规则：
     * 1. 空值/空字符串：直接返回，不处理
     * 2. 长度≤2：1位显示*，2位显示*+最后1位（如"12"→"*2"）
     * 3. 长度>2：
     *    - 短字符串（中间可脱敏位数<3）：首尾各留1位，中间全*（如"花溪大学城"→"花******
     城"）
     *    - 长字符串（中间可脱敏位数≥3）：首尾留等量字符，中间固定6个
     *（如"13900000000"→"1******0"）
     * @param value 待脱敏的原始字符串（如手机号、邮箱、地址等）
     * @return String 脱敏后的字符串
     */
    public static String commonDisplay(String value) {
        // 1. 空值处理：原始值为null或空字符串，直接返回（避免空指针）
        if (null == value || "".equals(value)) {
            return value;
        }
        // 2. 获取原始字符串长度，作为脱敏规则判断的核心依据
        int len = value.length();
        // 3. 计算中间分割点（字符串长度的一半）
        int pamaone = len / 2;
        // 4. 计算中间可脱敏的起始位置（分割点-1）
        int pamatwo = pamaone - 1;
        // 5. 计算字符串长度的奇偶性（1=奇数，0=偶数）
        int pamathree = len % 2;
        // 6. 构建脱敏后的字符串（StringBuilder效率高于String拼接）
        StringBuilder stringBuilder = new StringBuilder();
        // ========== 分支1：字符串长度≤2的脱敏规则 ==========
        if (len <= 2) {
            // 长度为1（奇数）：直接返回*（如"李"→"*"）
            if (pamathree == 1) {
                return SYMBOL;
            }
            // 长度为2（偶数）：第一位*，第二位保留（如"李华"→"*华"）
            stringBuilder.append(SYMBOL);
            stringBuilder.append(value.charAt(len - 1));
        }
        // ========== 分支2：字符串长度>2的脱敏规则 ==========
        else {
            // 子分支1：中间可脱敏位数≤0（短字符串，如长度3：pamatwo=3/2-1=0）
            if (pamatwo <= 0) {
                // 首尾各留1位，中间1个*（如"123"→"1*3"）
                stringBuilder.append(value.substring(0, 1));       // 取第一位
                stringBuilder.append(SYMBOL);                      // 中间加*
                stringBuilder.append(value.substring(len - 1, len)); // 取最后一位
            }
            // 子分支2：中间可脱敏位数≥3 且 字符串长度≠7（长字符串，固定中间6个*）
            else if (pamatwo >= SIZE / 2 && SIZE + 1 != len) {
                // 计算首尾保留的字符数（总长度-6个*，再平分给首尾）
                int pamafive = (len - SIZE) / 2;
                stringBuilder.append(value.substring(0, pamafive)); // 取前pamafive位
                // 中间固定添加6个*
                for (int i = 0; i < SIZE; i++) {
                    stringBuilder.append(SYMBOL);
                }
                // 根据奇偶性调整尾部保留的字符数，保证首尾字符数均衡
                if ((pamathree == 0 && SIZE / 2 == 0) || (pamathree != 0 && SIZE
                        % 2 != 0)) {
                    stringBuilder.append(value.substring(len - pamafive, len));
// 尾部留pamafive位
                } else {
                    stringBuilder.append(value.substring(len - (pamafive + 1),
                            len)); // 尾部留pamafive+1位
                }
            }
            // 子分支3：其他长度（中间可脱敏位数>0且<3，首尾各留1位，中间全*）
            else {
                int pamafour = len - 2; // 中间需要脱敏的位数（总长度-首尾各1位）
                stringBuilder.append(value.substring(0, 1));       // 取第一位
                // 中间添加对应位数的*
                for (int i = 0; i < pamafour; i++) {
                    stringBuilder.append(SYMBOL);
                }
                stringBuilder.append(value.substring(len - 1, len)); // 取最后一位
            }
        }
        // 7. 返回最终脱敏后的字符串
        return stringBuilder.toString();
    }
}
