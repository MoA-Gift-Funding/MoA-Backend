package moa.order.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.friend.domain.Friend;
import moa.friend.query.FriendQueryRepository;
import moa.member.domain.Member;
import moa.member.query.MemberQueryRepository;
import moa.order.domain.Order;
import moa.order.query.response.OrderDetailResponse;
import moa.order.query.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final MemberQueryRepository memberQueryRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final FriendQueryRepository friendQueryRepository;

    public Page<OrderResponse> findMyOrders(Long memberId, Pageable pageable) {
        return orderQueryRepository.findAllByMemberId(memberId, pageable)
                .map(OrderResponse::from);
    }

    public OrderDetailResponse findOrderById(Long memberId, Long orderId) {
        Member member = memberQueryRepository.getById(memberId);
        Order order = orderQueryRepository.getWithFundingAndProductById(orderId);
        order.validateOwner(member);
        List<Friend> friends = friendQueryRepository.findAllByMemberId(memberId);
        return OrderDetailResponse.of(order, friends);
    }
}
