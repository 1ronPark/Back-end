package umc.lightup.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import umc.lightup.api.ApiResponse;
import umc.lightup.notification.converter.NotificationConverter;
import umc.lightup.notification.domain.Notification;
import umc.lightup.notification.dto.NotificationResponseDTO;
import umc.lightup.notification.service.NotificationQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationRestController {

  private final NotificationQueryService notificationQueryService;

  @GetMapping("")
  @Operation(summary = "알림 목록 불러오기 API", description = "수신 받은 알림 조회를 위한 API 이며, 페이징을 포함합니다. query String으로 page 번호와 size 크기 값을 주세요")
  @ApiResponses({
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
  })
  public ApiResponse<NotificationResponseDTO.NotificationListDTO> getNotificationList(@Valid @RequestParam(name= "userId") Long userId, @RequestParam(name = "page") Integer page, @RequestParam(name = "size") Integer size){
    Page<Notification> notificationList = notificationQueryService.getNotificationList(userId, page, size);
    return ApiResponse.onSuccess(NotificationConverter.notificationListDTO(notificationList));
  }
}
