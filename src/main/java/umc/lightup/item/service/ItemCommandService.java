package umc.lightup.item.service;

import org.springframework.web.multipart.MultipartFile;
import umc.lightup.item.domain.Item;
import umc.lightup.item.dto.ItemRequestDTO;
import umc.lightup.member.domain.Member;

public interface ItemCommandService {
    Item createItem(Member member, ItemRequestDTO.ItemJoinRequestDTO request, MultipartFile file);
}
