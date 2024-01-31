package hg.userservice.presentation.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FollowActivityRequest {
    private List<Long> targetIdList;
}
