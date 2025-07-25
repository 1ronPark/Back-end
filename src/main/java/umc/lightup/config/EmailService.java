package umc.lightup.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.exception.handler.GeneralHandler;

import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;

    /**
     * HTML(MIME)의 형태로 이메일을 전송함
     * @param to 수신 주소(보낼 주소)
     * @param title 이메일의 제목
     * @param content 이메일의 내용(HTML 형식)
     * @throws GeneralHandler 이메일 전송 실패 시
     */
    @Async
    public void sendEmailMime(String to, String title, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setSubject(title);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setText(content, "utf-8", "html");
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new GeneralHandler(ErrorStatus.EMAIL_SEND_FAIL);
        }
    }

    /**
     * HTML(MIME)의 형태로 이메일을 전송함
     * @param to 수신 주소(보낼 주소)
     * @param title 이메일의 제목
     * @param templateName 템플릿 파일 이름
     * @param variables 템플릿 값 설정
     * @throws GeneralHandler 이메일 전송 실패 시
     */
    @SuppressWarnings("unchecked")
    @Async
    public void sendEmailTemplate(String to, String title, String templateName, Object variables) {
        Context context = new Context(Locale.KOREA, objectMapper.convertValue(variables, Map.class));
        String html = templateEngine.process(templateName, context);
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mail, true, "utf-8");
            message.setSubject(title);
            message.setTo(to);
            message.setText(html, true);
            javaMailSender.send(mail);
        } catch (Exception e) {
            throw new GeneralHandler(ErrorStatus.EMAIL_SEND_FAIL);
        }
    }
}