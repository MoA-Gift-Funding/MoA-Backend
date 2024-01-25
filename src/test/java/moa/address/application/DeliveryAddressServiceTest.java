package moa.address.application;

import static moa.address.exception.DeliveryAddressExceptionType.NO_AUTHORITY;
import static moa.address.exception.DeliveryAddressExceptionType.REQUIRED_DEFAULT_ADDRESS;
import static moa.fixture.MemberFixture.member;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import moa.address.application.command.AddressCreateCommand;
import moa.address.application.command.AddressUpdateCommand;
import moa.address.domain.Address;
import moa.address.domain.DeliveryAddress;
import moa.address.domain.DeliveryAddressRepository;
import moa.address.exception.DeliveryAddressException;
import moa.global.exception.MoaExceptionType;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.support.ApplicationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ApplicationTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("배송지 서비스 (DeliveryAddressService) 은(는)")
class DeliveryAddressServiceTest {

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(
                member(null, "mallang", "010-1111-1111", SIGNED_UP)
        );
    }

    @Nested
    class 배송지_생성_시 {

        @Test
        void 첫_생성인_경우_기본_배송지로_설정되지_않았다면_예외() {
            // given
            var command = new AddressCreateCommand(
                    member.getId(),
                    "말랑이네 집",
                    "신동훈",
                    "010-1111-2222",
                    "12345",
                    "땡땡시 땡땡구 땡떙로",
                    "땡땡시 땡땡구 떙떙동",
                    "땡땡아파트 1000동 1001호",
                    false
            );

            // when & then
            MoaExceptionType exceptionType = assertThrows(DeliveryAddressException.class, () ->
                    deliveryAddressService.create(command)
            ).getExceptionType();
            assertThat(exceptionType).isEqualTo(REQUIRED_DEFAULT_ADDRESS);
        }

        @Test
        void 이미_배송지가_존재하는_경우_기본_배송지가_아닌_상태로_등록이_가능하다() {
            // given
            deliveryAddressService.create(new AddressCreateCommand(
                    member.getId(),
                    "말랑이네 집",
                    "신동훈",
                    "010-1111-2222",
                    "12345",
                    "땡땡시 땡땡구 땡떙로",
                    "땡땡시 땡땡구 떙떙동",
                    "땡땡아파트 1000동 1001호",
                    true
            ));
            var command = new AddressCreateCommand(
                    member.getId(),
                    "주노네 집",
                    "최준호",
                    "010-2222-2222",
                    "12345",
                    "땡땡시 땡땡구 땡떙로",
                    "땡땡시 땡땡구 떙떙동",
                    "땡땡아파트 1000동 1001호",
                    false
            );

            // when
            Long id = deliveryAddressService.create(command);

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 이미_배송지가_존재하는_경우_새로_등록된_배송지로_기본_배송지_변경이_가능하다() {
            // given
            Long mallangAddressId = deliveryAddressService.create(new AddressCreateCommand(
                    member.getId(),
                    "말랑이네 집",
                    "신동훈",
                    "010-1111-2222",
                    "12345",
                    "땡땡시 땡땡구 땡떙로",
                    "땡땡시 땡땡구 떙떙동",
                    "땡땡아파트 1000동 1001호",
                    true
            ));
            var command = new AddressCreateCommand(
                    member.getId(),
                    "주노네 집",
                    "최준호",
                    "010-2222-2222",
                    "12345",
                    "땡땡시 땡땡구 땡떙로",
                    "땡땡시 땡땡구 떙떙동",
                    "땡땡아파트 1000동 1001호",
                    true
            );

            // when
            Long junoAddressId = deliveryAddressService.create(command);

            // then
            DeliveryAddress junos = deliveryAddressRepository.getById(junoAddressId);
            assertThat(junos.isDefault()).isTrue();
            DeliveryAddress mallangs = deliveryAddressRepository.getById(mallangAddressId);
            assertThat(mallangs.isDefault()).isFalse();
        }
    }

    @Nested
    class 배송지_수정_시 {

        private Long defaultDeliveryAddressId;

        @BeforeEach
        void setUp() {
            defaultDeliveryAddressId = deliveryAddressRepository.save(new DeliveryAddress(
                    member,
                    "말랑이네 집",
                    "신동훈",
                    "010-1111-2222",
                    new Address(
                            "12345",
                            "땡땡시 땡땡구 땡떙로",
                            "땡땡시 땡땡구 떙떙동",
                            "땡땡아파트 1000동 1001호"
                    ),
                    true
            )).getId();
        }

        @Test
        void 나의_주소지가_아니라면_예외() {
            // given
            Member juno = memberRepository.save(member(null, "주노", "010-3333-3333", SIGNED_UP));
            var command = new AddressUpdateCommand(
                    juno.getId(),
                    defaultDeliveryAddressId,
                    "준호네 집",
                    "준호",
                    "010-2222-2222",
                    "12345",
                    "땡땡시 땡땡구 땡떙로",
                    "땡땡시 땡땡구 떙떙동",
                    "땡땡아파트 1000동 1001호",
                    true
            );

            // when & then
            MoaExceptionType exceptionType = assertThrows(DeliveryAddressException.class, () -> {
                deliveryAddressService.update(command);
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(NO_AUTHORITY);
        }

        @Test
        void 기본_배송지로_변경할_수_있다() {
            // given
            DeliveryAddress junos = deliveryAddressRepository.save(new DeliveryAddress(
                    member,
                    "주노네 집",
                    "최준호",
                    "010-2222-2222",
                    new Address(
                            "12345",
                            "땡땡시 땡땡구 땡떙로",
                            "땡땡시 땡땡구 떙떙동",
                            "땡땡아파트 1000동 1001호"
                    ),
                    false
            ));
            var command = new AddressUpdateCommand(
                    member.getId(),
                    junos.getId(),
                    "준호네 집",
                    "준호",
                    "010-2222-2222",
                    "12345",
                    "땡땡시 땡땡구 땡떙로",
                    "땡땡시 땡땡구 떙떙동",
                    "땡땡아파트 1000동 1001호",
                    true
            );

            // when
            deliveryAddressService.update(command);

            // then
            DeliveryAddress updatedJunos = deliveryAddressRepository.getById(junos.getId());
            assertThat(updatedJunos.getName()).isEqualTo("준호네 집");
            assertThat(updatedJunos.isDefault()).isTrue();
            DeliveryAddress defaultDeliveryAddress = deliveryAddressRepository.getById(defaultDeliveryAddressId);
            assertThat(defaultDeliveryAddress.isDefault()).isFalse();
        }

        @Test
        void 기존의_기본_배송지_수정_시_기본_배송지로_설정되지_않은_경우_예외() {
            // given
            DeliveryAddress junos = deliveryAddressRepository.save(new DeliveryAddress(
                    member,
                    "주노네 집",
                    "최준호",
                    "010-2222-2222",
                    new Address(
                            "12345",
                            "땡땡시 땡땡구 땡떙로",
                            "땡땡시 땡땡구 떙떙동",
                            "땡땡아파트 1000동 1001호"
                    ),
                    false
            ));
            var command = new AddressUpdateCommand(
                    member.getId(),
                    defaultDeliveryAddressId,
                    "말랑이네 집",
                    "신동훈",
                    "010-1111-2222",
                    "12345",
                    "땡땡시 땡땡구 땡떙로",
                    "땡땡시 땡땡구 떙떙동",
                    "땡땡아파트 1000동 1001호",
                    false
            );

            // when & then
            MoaExceptionType exceptionType = assertThrows(DeliveryAddressException.class, () -> {
                deliveryAddressService.update(command);
            }).getExceptionType();
            assertThat(exceptionType).isEqualTo(REQUIRED_DEFAULT_ADDRESS);
        }
    }

    @Nested
    class 배송지_삭제_시 {

        private Long defaultDeliveryAddressId;

        @BeforeEach
        void setUp() {
            defaultDeliveryAddressId = deliveryAddressRepository.save(new DeliveryAddress(
                    member,
                    "말랑이네 집",
                    "신동훈",
                    "010-1111-2222",
                    new Address(
                            "12345",
                            "땡땡시 땡땡구 땡떙로",
                            "땡땡시 땡땡구 떙떙동",
                            "땡땡아파트 1000동 1001호"
                    ),
                    true
            )).getId();
        }

        @Test
        void 기본_배송지는_삭제할_수_없다() {
            // when & then
            MoaExceptionType exceptionType = assertThrows(DeliveryAddressException.class, () ->
                    deliveryAddressService.delete(member.getId(), defaultDeliveryAddressId)
            ).getExceptionType();
            assertThat(exceptionType).isEqualTo(REQUIRED_DEFAULT_ADDRESS);
        }

        @Test
        void 기본_배송지가_아니면_삭제된다() {
            // given
            deliveryAddressService.create(
                    new AddressCreateCommand(
                            member.getId(),
                            "주노네 집",
                            "최준호",
                            "010-2222-2222",
                            "12345",
                            "땡땡시 땡땡구 땡떙로",
                            "땡땡시 땡땡구 떙떙동",
                            "땡땡아파트 1000동 1001호",
                            true
                    )
            );

            // when
            deliveryAddressService.delete(member.getId(), defaultDeliveryAddressId);

            // then
            assertThat(deliveryAddressRepository.existsById(defaultDeliveryAddressId)).isFalse();
        }
    }
}
