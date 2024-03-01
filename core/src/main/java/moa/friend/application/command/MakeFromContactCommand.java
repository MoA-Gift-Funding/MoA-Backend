package moa.friend.application.command;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

public record MakeFromContactCommand(
        Long memberId,
        List<ContactInfo> contactList
) {
    public record ContactInfo(
            String name,
            String phoneNumber
    ) {
    }

    public Map<String, String> phoneAndNameMap() {
        return contactList.stream()
                .collect(toMap(
                        ContactInfo::phoneNumber,
                        ContactInfo::name,
                        (order, recent) -> recent
                ));
    }

    public List<String> phones() {
        return contactList.stream()
                .map(it -> it.phoneNumber)
                .toList();
    }
}
