package io.github.wiriswernek.library_api.service.imp;

import io.github.wiriswernek.library_api.model.entity.LoanEntity;
import io.github.wiriswernek.library_api.service.EmailService;
import io.github.wiriswernek.library_api.service.LoanService;
import io.github.wiriswernek.library_api.service.ScheduleService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImp implements ScheduleService {

    @Value("${spring.application.mail.lateloans.message}")
    private String message;

    private final LoanService loanService;
    private final EmailService emailService;

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * *";

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendEmailToLAteLoans(){
        List<LoanEntity> loans = loanService.getAllLateLoans();
        List<String> emails = loans.stream().map( l -> l.getCustomerEmail() ).collect(Collectors.toList());

        emailService.sendMails(message, emails);
    }
}
