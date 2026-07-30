package com.runner.wallet.mapper;

import com.runner.my.mapper.MyMapper;
import com.runner.pojo.WalletTransaction;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionMapper extends MyMapper<WalletTransaction> {
}