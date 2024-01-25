package moa.address.domain;

import static moa.address.exception.DeliveryAddressExceptionType.REQUIRED_DEFAULT_ADDRESS;

import java.util.ArrayList;
import java.util.List;
import moa.address.exception.DeliveryAddressException;

public class AddressBook {

    private final List<DeliveryAddress> addresses = new ArrayList<>();

    public AddressBook(List<DeliveryAddress> addresses) {
        this.addresses.addAll(addresses);
    }

    public void add(DeliveryAddress deliveryAddress) {
        if (deliveryAddress.isDefault()) {
            makeAllUnDefault();
            return;
        }
        if (addresses.isEmpty()) {
            throw new DeliveryAddressException(REQUIRED_DEFAULT_ADDRESS);
        }
    }

    private void makeAllUnDefault() {
        for (DeliveryAddress address : addresses) {
            address.unDefault();
        }
    }
}
