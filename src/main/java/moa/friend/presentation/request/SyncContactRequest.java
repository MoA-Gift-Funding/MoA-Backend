package moa.friend.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.List;
import moa.friend.application.command.MakeFromContactCommand;
import moa.friend.application.command.MakeFromContactCommand.ContactInfo;

public record SyncContactRequest(
        List<ContactRequest> contactList
) {
    public SyncContactRequest(ContactRequest... contactList) {
        this(Arrays.asList(contactList));
    }

    public record ContactRequest(
            @Schema(example = "말랑") String name,
            @Schema(example = "010-1234-5678") String phoneNumber
    ) {
    }

    public MakeFromContactCommand toCommand(Long memberId) {
        return new MakeFromContactCommand(memberId,
                contactList.stream()
                        .map(it -> new ContactInfo(it.name, it.phoneNumber))
                        .toList()
        );
    }
}
