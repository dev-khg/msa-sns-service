package hg.activityservice.core.service.external.newfeed.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRequest {
    private List<Long> targetIdList;
}
