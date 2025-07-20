package umc.lightup.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import umc.lightup.api.ApiResponse;
import umc.lightup.member.domain.Member;
import umc.lightup.member.service.MemberCommandService;
import umc.lightup.notification.converter.NotificationConverter;
import umc.lightup.notification.domain.Notification;
import umc.lightup.notification.dto.NotificationResponseDTO;
import umc.lightup.notification.service.NotificationQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationRestController {

  private final NotificationQueryService notificationQueryService;
  private final MemberCommandService memberCommandService;

  @GetMapping("")
  @Operation(
          summary = "알림 목록 불러오기 API",
          description = "수신 받은 알림 조회를 위한 API 이며, 페이징을 포함합니다. query String으로 page 번호와 size 크기 값을 주세요",
          security = { @SecurityRequirement(name = "JWT TOKEN")}
  )
  @ApiResponses({
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
  })
  public ApiResponse<NotificationResponseDTO.NotificationListDTO> getNotificationList(Authentication authentication, @NotNull @Min(0) @RequestParam(name = "page") Integer page, @Min(1) @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){
    Member member = memberCommandService.getMember(authentication.getName());
    Page<Notification> notificationList = notificationQueryService.getNotificationList(member, page, size);
    return ApiResponse.onSuccess(NotificationConverter.notificationListDTO(notificationList));
  }
}
