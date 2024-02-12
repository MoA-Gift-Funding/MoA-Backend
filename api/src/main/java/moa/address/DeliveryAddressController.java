package moa.address;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.address.application.DeliveryAddressService;
import moa.address.query.DeliveryAddressQueryService;
import moa.address.query.response.AddressResponse;
import moa.address.request.AddressCreateRequest;
import moa.address.request.AddressUpdateRequest;
import moa.auth.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/addresses")
public class DeliveryAddressController implements DeliveryAddressApi {

    private final DeliveryAddressService deliveryAddressService;
    private final DeliveryAddressQueryService deliveryAddressQueryService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Auth(permit = SIGNED_UP) Long memberId,
            @Valid @RequestBody AddressCreateRequest request
    ) {
        Long id = deliveryAddressService.create(request.toCommand(memberId));
        return ResponseEntity.created(URI.create("/addresses/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @Auth(permit = SIGNED_UP) Long memberId,
            @PathVariable("id") Long id,
            @Valid @RequestBody AddressUpdateRequest request
    ) {
        deliveryAddressService.update(request.toCommand(memberId, id));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Auth(permit = SIGNED_UP) Long memberId,
            @PathVariable("id") Long id
    ) {
        deliveryAddressService.delete(memberId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> findMy(
            @Auth(permit = SIGNED_UP) Long memberId
    ) {
        List<AddressResponse> result = deliveryAddressQueryService.findByMemberId(memberId);
        return ResponseEntity.ok(result);
    }
}
