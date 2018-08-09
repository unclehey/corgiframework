package pers.corgiframework.dao.mapper;

import pers.corgiframework.dao.domain.PaymentOrder;
import pers.corgiframework.dao.domain.PaymentOrderExample;

import java.util.List;

public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {
    List<PaymentOrder> selectByExample(PaymentOrderExample example);
}