package umc.lightup.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import umc.lightup.api.ApiResponse;
import umc.lightup.member.domain.Member;
import umc.lightup.member.service.MemberCommandService;
import umc.lightup.notification.converter.NotificationConverter;
import umc.lightup.notification.domain.Notification;
import umc.lightup.notification.dto.NotificationResponseDTO;
import umc.lightup.notification.service.NotificationCommandService;
import umc.lightup.notification.service.NotificationQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationRestController {

  private final NotificationQueryService notificationQueryService;
  private final NotificationCommandService notificationCommandService;
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

  @DeleteMapping("/{notificationId}")
  @Operation(
          summary = "알림 지우기 API",
          description = "읽은 알림을 지우기 위한 API 입니다. notificationId를 보내면 삭제됩니다. ",
          security = { @SecurityRequirement(name = "JWT TOKEN")}
  )
  @ApiResponses({
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
  })
  public ApiResponse<NotificationResponseDTO.NotificationDeleteDTO> deletNotification(@NotNull @PathVariable(name = "notificationId") long notificationId){
    Notification notification = notificationCommandService.deleteNotification(notificationId);
    return ApiResponse.onSuccess(NotificationConverter.notificationDeleteDTO(notification));
  }


  @GetMapping("/subscribe")
  @Operation(
          summary = "SSE 통신을 위한 구독용 API",
          description = "SSE 통신을 위한 구독용 API 입니다. Last-Event_ID는 선택사항으로 해당 사용자에 대한 미수신 알림이 있다면 보내주기 위함입니다. ",
          security = { @SecurityRequirement(name = "JWT TOKEN")}
  )
  @ApiResponses({
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
  })
  public SseEmitter subscribe(
          Authentication authentication,
          @RequestHeader(value = "Last-Event_ID", required = false, defaultValue = "") final String lastEventId
  ){
    Member member = memberCommandService.getMember(authentication.getName());
    return notificationQueryService.subscribe(member, lastEventId);
  }


  @PostMapping("/test/{receiverId}")
  @Operation(
          summary = "SSE 알림 전송 테스트 API",
          description = "SSE 알림 전송 테스트를 위한 API 입니다. RecieverID를 자기자신으로 설정하면 자기 자신한테 옵니다.",
          security = { @SecurityRequirement(name = "JWT TOKEN")}
  )
  @ApiResponses({
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
  })
  public ApiResponse<NotificationResponseDTO.SSETestDTO> testSSE(@NotNull @PathVariable(name = "receiverId") Long receiverId, @RequestParam(name = "message") String message){
    return ApiResponse.onSuccess(notificationQueryService.testSend(receiverId, message));
  }

}
