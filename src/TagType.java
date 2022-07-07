//Written by 小田竜太郎

public enum TagType {//TODO:
    EnterRoom,
    MEMBER,
    NewMember,
    ChatRoom,
    READY,
    ShareStart,
    ShareName,
    ShareRole,
    ShareIdentifier,
    ShareColor,
    ShareNext,
    ChatGame,
    PhantomThief,
    DayTime,
    CO,
    VOTE,
    GameEnd;

    public String getTag() {
        return this.name() + ":";
    }

    public int getCharCount() {
        return (this.name() + ":").length();

    }
}
