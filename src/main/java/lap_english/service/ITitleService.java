package lap_english.service;

import lap_english.dto.UserTitleDto;

public interface ITitleService {
    UserTitleDto claimTitle(Long titleId);
}
