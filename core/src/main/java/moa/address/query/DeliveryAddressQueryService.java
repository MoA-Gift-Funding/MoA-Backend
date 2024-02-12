package moa.address.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.address.query.response.AddressResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryAddressQueryService {

    private final DeliveryAddressQueryRepository addressRepository;

    public List<AddressResponse> findByMemberId(Long memberId) {
        return addressRepository.findByMemberId(memberId)
                .stream()
                .map(AddressResponse::from)
                .toList();
    }
}
