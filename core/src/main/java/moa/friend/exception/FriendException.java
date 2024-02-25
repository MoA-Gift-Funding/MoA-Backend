package moa.friend.exception;

import moa.global.exception.MoaException;
import moa.global.exception.MoaExceptionType;

public class FriendException extends MoaException {

    private final FriendExceptionType friendExceptionType;

    public FriendException(FriendExceptionType friendExceptionType) {
        super(friendExceptionType);
        this.friendExceptionType = friendExceptionType;
    }

    @Override
    public MoaExceptionType getExceptionType() {
        return friendExceptionType;
    }
}
