package com.runner.wallet.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.runner.exception.GraceException;
import com.runner.grace.result.GraceJSONResult;
import com.runner.grace.result.ResponseStatusEnum;
import com.runner.pojo.AppUser;
import com.runner.pojo.WalletTransaction;
import com.runner.wallet.config.AlipayConfig;
import com.runner.wallet.mapper.AppUserMapper;
import com.runner.wallet.mapper.WalletTransactionMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Api(tags = "钱包管理")
@RestController
@RequestMapping("wallet")
public class WalletController {

    @Autowired
    private WalletTransactionMapper walletTransactionMapper;

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private AppUserMapper appUserMapper;

    // ========== 查询余额 ==========
    @ApiOperation("查询余额")
    @GetMapping("/balance")
    public GraceJSONResult getBalance(@RequestParam String userId) {
        List<WalletTransaction> transactions = getTransactionsByUserId(userId);
        BigDecimal balance = BigDecimal.ZERO;
        for (WalletTransaction tx : transactions) {
            if (tx.getStatus() == 1) {
                if (tx.getType() == 1 || tx.getType() == 3 || tx.getType() == 5) {
                    balance = balance.add(tx.getAmount());
                } else if (tx.getType() == 2 || tx.getType() == 4) {
                    balance = balance.subtract(tx.getAmount());
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("balance", balance);
        result.put("userId", userId);
        return GraceJSONResult.ok(result);
    }

    // ========== 更新余额（统一方法） ==========
    private void updateBalance(String userId, BigDecimal amount) {
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) return;
        BigDecimal newBalance = (user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO).add(amount);
        user.setBalance(newBalance);
        user.setUpdatedTime(new Date());
        appUserMapper.updateByPrimaryKeySelective(user);
    }

    @ApiOperation("查询交易记录")
    @GetMapping("/transactions")
    public GraceJSONResult getTransactions(@RequestParam String userId,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(page, pageSize, "created_time DESC");
        WalletTransaction condition = new WalletTransaction();
        condition.setUserId(userId);
        List<WalletTransaction> list = walletTransactionMapper.select(condition);
        PageInfo<WalletTransaction> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("page", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());

        return GraceJSONResult.ok(result);
    }

    @ApiOperation("查询订单状态（用于前端轮询）")
    @GetMapping("/orderStatus")
    public GraceJSONResult getOrderStatus(@RequestParam String orderNo) {
        WalletTransaction condition = new WalletTransaction();
        condition.setOrderNo(orderNo);
        List<WalletTransaction> list = walletTransactionMapper.select(condition);
        if (list != null && !list.isEmpty()) {
            WalletTransaction tx = list.get(0);
            Map<String, Object> result = new HashMap<>();
            result.put("status", tx.getStatus());
            result.put("amount", tx.getAmount());
            result.put("orderNo", tx.getOrderNo());
            return GraceJSONResult.ok(result);
        }
        return GraceJSONResult.errorMsg("订单不存在");
    }

    // ========== 充值 ==========

    @ApiOperation("发起充值（返回订单号）")
    @PostMapping("/recharge")
    public GraceJSONResult recharge(@RequestParam String userId,
                                    @RequestParam BigDecimal amount,
                                    @RequestParam String subject) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        String orderNo = System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        transaction.setUserId(userId);
        transaction.setType(1);
        transaction.setAmount(amount);
        transaction.setDescription("支付宝充值：" + subject);
        transaction.setStatus(0);
        transaction.setOrderNo(orderNo);
        transaction.setCreatedTime(new Date());
        transaction.setUpdatedTime(new Date());
        walletTransactionMapper.insert(transaction);

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", orderNo);
        result.put("amount", amount);

        return GraceJSONResult.ok(result);
    }

    @ApiOperation("跳转支付宝支付")
    @PostMapping("/pay")
    public void pay(@RequestParam String orderNo, HttpServletResponse response) throws IOException {
        WalletTransaction condition = new WalletTransaction();
        condition.setOrderNo(orderNo);
        List<WalletTransaction> list = walletTransactionMapper.select(condition);
        if (list == null || list.isEmpty()) {
            response.getWriter().write("订单不存在");
            return;
        }

        WalletTransaction transaction = list.get(0);

        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfig.getGatewayUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                alipayConfig.getFormat(),
                alipayConfig.getCharset(),
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getSignType()
        );

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(alipayConfig.getReturnUrl() + "?orderNo=" + orderNo);
        request.setNotifyUrl(alipayConfig.getNotifyUrl());

        request.setBizContent("{" +
                "\"out_trade_no\":\"" + orderNo + "\"," +
                "\"total_amount\":\"" + transaction.getAmount() + "\"," +
                "\"subject\":\"" + transaction.getDescription() + "\"," +
                "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        try {
            String form = alipayClient.pageExecute(request).getBody();
            response.setContentType("text/html;charset=" + alipayConfig.getCharset());
            response.getWriter().write(form);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            GraceException.display(ResponseStatusEnum.FAILED);
        }
    }

    // ========== 充值确认 ==========
    @ApiOperation("确认充值（支付宝同步回调调用）")
    @GetMapping("/confirmRecharge")
    public GraceJSONResult confirmRecharge(@RequestParam String orderNo) {
        WalletTransaction condition = new WalletTransaction();
        condition.setOrderNo(orderNo);
        List<WalletTransaction> list = walletTransactionMapper.select(condition);
        if (list != null && !list.isEmpty()) {
            WalletTransaction transaction = list.get(0);
            // 只有状态为0且创建时间在30分钟内的才允许确认
            long diff = System.currentTimeMillis() - transaction.getCreatedTime().getTime();
            if (transaction.getStatus() == 0) {
                if (diff > 30 * 60 * 1000) {
                    transaction.setStatus(2); // 超时失败
                    transaction.setUpdatedTime(new Date());
                    walletTransactionMapper.updateByPrimaryKeySelective(transaction);
                    return GraceJSONResult.errorMsg("订单已超时，请重新充值");
                }
                // 更新余额
                updateBalance(transaction.getUserId(), transaction.getAmount());
                transaction.setStatus(1);
                transaction.setUpdatedTime(new Date());
                walletTransactionMapper.updateByPrimaryKeySelective(transaction);
                return GraceJSONResult.ok("充值确认成功，余额已增加");
            }
            return GraceJSONResult.ok("该订单已处理");
        }
        return GraceJSONResult.errorMsg("订单不存在");
    }

    // ========== 提现 ==========

    @ApiOperation("申请提现（自动打款）")
    @PostMapping("/withdraw")
    public GraceJSONResult withdraw(@RequestParam String userId,
                                    @RequestParam BigDecimal amount,
                                    @RequestParam String alipayAccount,
                                    @RequestParam String realName) {
        // 1. 校验金额
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        // 2. 查询余额
        BigDecimal balance = getCurrentBalance(userId);
        if (balance.compareTo(amount) < 0) {
            GraceException.display(ResponseStatusEnum.INSUFFICIENT_BALANCE);
        }

        // 3. 生成订单号
        String orderNo = System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);

        // 4. 创建提现记录（状态：处理中）
        WalletTransaction transaction = new WalletTransaction();
        transaction.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        transaction.setUserId(userId);
        transaction.setType(2); // 提现
        transaction.setAmount(amount);
        transaction.setDescription("提现到支付宝：" + alipayAccount);
        transaction.setStatus(0);
        transaction.setOrderNo(orderNo);
        transaction.setCreatedTime(new Date());
        transaction.setUpdatedTime(new Date());
        walletTransactionMapper.insert(transaction);

        // 5. 调用支付宝转账接口（自动打款）
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                    alipayConfig.getGatewayUrl(),
                    alipayConfig.getAppId(),
                    alipayConfig.getPrivateKey(),
                    alipayConfig.getFormat(),
                    alipayConfig.getCharset(),
                    alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getSignType()
            );

            AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
            request.setBizContent("{" +
                    "\"out_biz_no\":\"" + orderNo + "\"," +
                    "\"payee_type\":\"ALIPAY_LOGONID\"," +
                    "\"payee_account\":\"" + alipayAccount + "\"," +
                    "\"amount\":\"" + amount + "\"," +
                    "\"payer_show_name\":\"校园闪电侠\"," +
                    "\"remark\":\"跑腿平台提现\"}");

            AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);

            // 6. 更新交易状态
            if (response.isSuccess()) {
                transaction.setStatus(1); // 成功
                transaction.setRemark("转账成功");
            } else {
                transaction.setStatus(2); // 失败
                transaction.setRemark("支付宝返回失败：" + response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            transaction.setStatus(2);
            transaction.setRemark("支付宝异常：" + e.getErrMsg());
        }

        // 更新余额（直接修改交易记录的balance字段）
        BigDecimal newBalance = getCurrentBalance(userId);
        if (transaction.getStatus() == 1) {
            // 提现成功，余额减少
            newBalance = newBalance.subtract(amount);
        }
        transaction.setBalance(newBalance);
        transaction.setUpdatedTime(new Date());
        walletTransactionMapper.updateByPrimaryKeySelective(transaction);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", orderNo);
        result.put("status", transaction.getStatus());
        result.put("amount", amount);

        if (transaction.getStatus() == 1) {
            return GraceJSONResult.ok(result);
        } else {
            return GraceJSONResult.errorMsg(transaction.getRemark() != null ? transaction.getRemark() : "提现失败，请重试");
        }
    }

    // ========== 收入增加 ==========
    @ApiOperation("收入增加（任务完成后调用）")
    @PostMapping("/addIncome")
    public GraceJSONResult addIncome(@RequestParam String userId,
                                     @RequestParam BigDecimal amount,
                                     @RequestParam String description) {
        WalletTransaction transaction = new WalletTransaction();
        transaction.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        transaction.setUserId(userId);
        transaction.setType(3);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setStatus(1);
        transaction.setCreatedTime(new Date());
        transaction.setUpdatedTime(new Date());
        walletTransactionMapper.insert(transaction);

        // 更新余额
        updateBalance(userId, amount);

        return GraceJSONResult.ok();
    }

    // ========== 支出扣除 ==========
    @ApiOperation("支出扣除（发布任务时调用）")
    @PostMapping("/deduct")
    public GraceJSONResult deduct(@RequestParam String userId,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam String description) {
        BigDecimal balance = getCurrentBalance(userId);
        if (balance.compareTo(amount) < 0) {
            GraceException.display(ResponseStatusEnum.INSUFFICIENT_BALANCE);
        }

        WalletTransaction transaction = new WalletTransaction();
        transaction.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        transaction.setUserId(userId);
        transaction.setType(4);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setStatus(1);
        transaction.setCreatedTime(new Date());
        transaction.setUpdatedTime(new Date());
        walletTransactionMapper.insert(transaction);

        // 更新余额
        updateBalance(userId, amount.negate());

        return GraceJSONResult.ok();
    }

    // ========== 退款 ==========
    @ApiOperation("退款（任务取消时调用）")
    @PostMapping("/refund")
    public GraceJSONResult refund(@RequestParam String userId,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam String taskId,
                                  @RequestParam String reason) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        WalletTransaction transaction = new WalletTransaction();
        transaction.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        transaction.setUserId(userId);
        transaction.setType(5);
        transaction.setAmount(amount);
        transaction.setDescription("任务取消退款: " + taskId + " (" + reason + ")");
        transaction.setStatus(1);
        transaction.setOrderNo("REFUND_" + System.currentTimeMillis());
        transaction.setCreatedTime(new Date());
        transaction.setUpdatedTime(new Date());
        walletTransactionMapper.insert(transaction);

        // 更新余额
        updateBalance(userId, amount);

        return GraceJSONResult.ok("退款成功");
    }

    // ========== 私有方法 ==========

    private List<WalletTransaction> getTransactionsByUserId(String userId) {
        WalletTransaction condition = new WalletTransaction();
        condition.setUserId(userId);
        return walletTransactionMapper.select(condition);
    }

    private BigDecimal getCurrentBalance(String userId) {
        List<WalletTransaction> transactions = getTransactionsByUserId(userId);
        BigDecimal balance = BigDecimal.ZERO;
        for (WalletTransaction tx : transactions) {
            if (tx.getStatus() == 1) {
                if (tx.getType() == 1 || tx.getType() == 3 || tx.getType() == 5) {
                    balance = balance.add(tx.getAmount());
                } else if (tx.getType() == 2 || tx.getType() == 4) {
                    balance = balance.subtract(tx.getAmount());
                }
            }
        }
        return balance;
    }

    private void addUserBalanceForTransaction(WalletTransaction transaction) {
        WalletTransaction income = new WalletTransaction();
        income.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        income.setUserId(transaction.getUserId());
        income.setType(3);
        income.setAmount(transaction.getAmount());
        income.setBalance(getCurrentBalance(transaction.getUserId()).add(transaction.getAmount()));
        income.setDescription("支付宝充值到账（订单：" + transaction.getOrderNo() + "）");
        income.setStatus(1);
        income.setCreatedTime(new Date());
        income.setUpdatedTime(new Date());
        walletTransactionMapper.insert(income);
    }
    @ApiOperation("获取跑腿员统计数据")
    @GetMapping("/runnerStats")
    public GraceJSONResult runnerStats(@RequestParam String userId) {
        List<WalletTransaction> transactions = getTransactionsByUserId(userId);
        BigDecimal totalEarnings = BigDecimal.ZERO;
        int completedCount = 0;

        for (WalletTransaction tx : transactions) {
            if (tx.getStatus() == 1 && tx.getType() == 3) {
                totalEarnings = totalEarnings.add(tx.getAmount());
                completedCount++;
            }
        }

        // 计算今日、本周、本月收入
        BigDecimal todayEarnings = BigDecimal.ZERO;
        BigDecimal weekEarnings = BigDecimal.ZERO;
        BigDecimal monthEarnings = BigDecimal.ZERO;

        Calendar now = Calendar.getInstance();

        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);

        Calendar weekStart = Calendar.getInstance();
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        weekStart.set(Calendar.HOUR_OF_DAY, 0);
        weekStart.set(Calendar.MINUTE, 0);
        weekStart.set(Calendar.SECOND, 0);
        weekStart.set(Calendar.MILLISECOND, 0);

        Calendar monthStart = Calendar.getInstance();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        monthStart.set(Calendar.HOUR_OF_DAY, 0);
        monthStart.set(Calendar.MINUTE, 0);
        monthStart.set(Calendar.SECOND, 0);
        monthStart.set(Calendar.MILLISECOND, 0);

        for (WalletTransaction tx : transactions) {
            if (tx.getStatus() == 1 && tx.getType() == 3) {
                Date createTime = tx.getCreatedTime();
                if (createTime.after(todayStart.getTime())) {
                    todayEarnings = todayEarnings.add(tx.getAmount());
                }
                if (createTime.after(weekStart.getTime())) {
                    weekEarnings = weekEarnings.add(tx.getAmount());
                }
                if (createTime.after(monthStart.getTime())) {
                    monthEarnings = monthEarnings.add(tx.getAmount());
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalEarnings", totalEarnings);
        result.put("completedCount", completedCount);
        result.put("todayEarnings", todayEarnings);
        result.put("weekEarnings", weekEarnings);
        result.put("monthEarnings", monthEarnings);

        return GraceJSONResult.ok(result);
    }
}