package umc.lightup.member.enums;

public enum Mbti {
    ISTJ,
    ISFJ,
    INFJ,
    INTJ,
    ISTP,
    ISFP,
    INFP,
    INTP,
    ESTP,
    ESFP,
    ENFP,
    ENTP,
    ESTJ,
    ESFJ,
    ENFJ,
    ENTJ;
    /*
    * E +8
    * N +4
    * F +2
    * P +1
    * 총 0~15로 표현
    * */

    public byte toByte() {
        byte[] textValue = this.toString().getBytes();
        byte result = 0;
        if (textValue[0] == 'E') result = 0x08;
        if (textValue[1] == 'N') result |= 0x04;
        if (textValue[2] == 'F') result |= 0x02;
        if (textValue[3] == 'P') result |= 0x01;
        return result;
    }

    public static Mbti fromByte(byte b) {
        StringBuilder textValue = new StringBuilder();
        if ((b & 0x08) != 0) textValue.append("E");
        else textValue.append("I");

        if ((b & 0x04) != 0) textValue.append("N");
        else textValue.append("S");

        if ((b & 0x02) != 0) textValue.append("F");
        else textValue.append("T");

        if ((b & 0x01) != 0) textValue.append("P");
        else textValue.append("J");

        return Mbti.valueOf(textValue.toString());
    }
}
