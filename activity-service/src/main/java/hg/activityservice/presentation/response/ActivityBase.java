package hg.activityservice.presentation.response;

import com.example.commonproject.activity.ActivityType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class ActivityBase implements ActivityResponse {
    protected ActivityType type;

    public ActivityBase(ActivityType type) {
        this.type = type;
    }
}
