package enums;

public enum StatePicMap {

    OFFLINE_PIC(State.OFFLINE, "client_offline3.png"),

    ONLINE_PIC(State.ONLINE, "client_offline2.png"),

    OK_PIC(State.OK, "client_online2.png");


    private final State state;

    private final String pic;


    StatePicMap(State state, String pic) {
        this.state = state;
        this.pic = pic;
    }

    public static String getPicByState(State state) {
        for (StatePicMap value : StatePicMap.values()) {
            if (state.equals(value.state)) {
                return value.pic;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        StatePicMap.getPicByState(State.OK);
    }
}
