package moa.address.application;

import lombok.RequiredArgsConstructor;
import moa.address.application.command.AddressCreateCommand;
import moa.address.domain.AddressBook;
import moa.address.domain.DeliveryAddress;
import moa.address.domain.DeliveryAddressRepository;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final MemberRepository memberRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;

    public Long create(AddressCreateCommand command) {
        Member member = memberRepository.getById(command.memberId());
        DeliveryAddress address = command.toAddress(member);
        AddressBook addressBook = deliveryAddressRepository.getAddressBookByMember(member);
        addressBook.add(address);
        return deliveryAddressRepository.save(address)
                .getId();
    }
}
