package moa.address.query;

import static moa.fixture.MemberFixture.member;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import moa.ApplicationTest;
import moa.address.domain.Address;
import moa.address.domain.DeliveryAddress;
import moa.address.domain.DeliveryAddressRepository;
import moa.address.query.response.AddressResponse;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ApplicationTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("배송지 조회 서비스 (DeliveryAddressQueryService) 은(는)")
class DeliveryAddressQueryServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Autowired
    private DeliveryAddressQueryService deliveryAddressQueryService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(
                member(null, "mallang", "010-1111-1111", SIGNED_UP)
        );
        deliveryAddressRepository.save(new DeliveryAddress(
                member,
                new Address(
                        "12345",
                        "땡땡시 땡땡구 땡떙로",
                        "땡땡시 땡땡구 떙떙동",
                        "땡땡아파트 1000동 1001호",
                        "말랑이네 집",
                        "신동훈",
                        "010-1111-2222"
                ),
                true
        ));
    }

    @Test
    void 회원_ID로_배송지를_조회한다() {
        // when
        List<AddressResponse> result = deliveryAddressQueryService.findByMemberId(member.getId());

        // then
        assertThat(result).hasSize(1);
    }
}
