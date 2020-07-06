package ru.alpha.task2.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.alpha.task2.model.dto.PaymentDto;
import ru.alpha.task2.model.dto.StatisticPaymentDto;
import ru.alpha.task2.model.exception.AlphaTaskException;
import ru.alpha.task2.model.exception.ExceptionMessage;
import ru.alpha.task2.service.IAgregationService;
import ru.alpha.task2.service.IDataService;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor
public class AgregationService implements IAgregationService {

    private IDataService dataService;

    @Override
    public List<StatisticPaymentDto> getAllAnalytics() {
        return getStat(dataService.getAllPayments());
    }

    @Override
    public StatisticPaymentDto getAnalyticByUserId(String userId) {
        List<PaymentDto> paymentDtos = dataService
                .getAllPaymentsByUserId(userId);
        return getStat(paymentDtos).get(0);
    }

    private List<StatisticPaymentDto> getStat(List<PaymentDto> paymentDtos) {
        return paymentDtos
                .stream()
                .collect(
                        Collectors.groupingBy(
                                PaymentDto::getUserId,
                                Collectors.groupingBy(
                                        PaymentDto::getCategoryId,
                                        Collectors.toList()
                                )
                        )
                )
                .entrySet()
                .stream()
                .map(user -> {
                    Map<Long, List<PaymentDto>> payIdToList = user.getValue();

                    final Map<String, StatisticPaymentDto.AnalyticInfo> map = new TreeMap<>();

                    Double total = payIdToList.entrySet()
                            .stream()
                            .map(category -> {
                                List<PaymentDto> paymentsDto = category.getValue();
                                double sum = paymentsDto.stream().map(PaymentDto::getAmount).reduce((u, v) -> u + v).orElse(0.0);

                                StatisticPaymentDto.AnalyticInfo info = StatisticPaymentDto.AnalyticInfo
                                        .builder()
                                        .sum(Math.floor(sum))
                                        .max(paymentsDto.stream().max(Comparator.comparing(PaymentDto::getAmount)).orElseGet(PaymentDto::new).getAmount())
                                        .min(paymentsDto.stream().min(Comparator.comparing(PaymentDto::getAmount)).orElseGet(PaymentDto::new).getAmount())
                                        .build();

                                map.put(category.getKey().toString(), info);

                                return sum;
                            })
                            .reduce((u, v) -> u + v)
                            .orElse(0.0);

                    return StatisticPaymentDto.builder()
                            .userId(user.getKey())
                            .totalSum(total)
                            .analyticInfo(map)
                            .build();
                })
                .collect(Collectors.toList());
    }
}

