package com.aa65535.tabikaeruarchivemodifier.utils;

public interface Constants {
    int VERSION_MIN = 10400;
    int VERSION_105 = 10500;
    int VERSION_106 = 10600;
    int VERSION_120 = 12000;
    int VERSION_121 = 12100;
    int VERSION_140 = 14000;
    int VERSION_150 = 15000;

    int SHORT_STR_LEN = 0x14;
    int STR_LEN = 0x28;

    int MAX_ALBUM_PAGE = 1014;

    long ACHIEVE_FLAGS_BITS = 0x1fcf;
    int ACHIEVE_FLAGS_BITS_LEN = 13;

    int[] ACHIEVE_FLAGS_IDS = new int[]{100000};

    long COLLECT_FLAGS_BITS = 0x1f7fffe;
    int COLLECT_FLAGS_BITS_LEN = 25;

    int[] COLLECT_FLAGS_IDS = new int[]{100000};

    long SPECIALTY_FLAGS_BITS = 0xffffff7fffeL;
    int SPECIALTY_FLAGS_BITS_LEN = 44;

    int[] SPECIALTY_FLAGS_IDS = new int[]{100000};

    int[] ALL_ITEMS_ARRAY = new int[]{
            0x00000000, 0x00000001, 0x00000002, 0x00000003, 0x00000004, 0x00000005, 0x00000006, 0x00000007,
            0x00000008, 0x00000009, 0x0000000a, 0x0000000b, 0x0000000c, 0x0000000d, 0x0000000e,
            0x000003e8, 0x800003e9, 0x000003ea, 0x000003eb, 0x000003ec, 0x000003ed, 0x000003ee, 0x000003ef,
            0x000003f0, 0x000003f1, 0x000003f2, 0x00018a88,
            0x800007d0, 0x800007d1, 0x800007d2, 0x800007d3, 0x800007d4, 0x800007d5, 0x800007d6, 0x800007d7,
            0x800007d8, 0x800007d9, 0x800007da, 0x800007db, 0x800007dc, 0x800007dd, 0x800007de,
            0x00000bb9, 0x00000bba, 0x00000bbb, 0x00000bbc, 0x00000bbd, 0x00000bbe, 0x00000bbf, 0x00000bc0,
            0x00000bc1, 0x00000bc2, 0x00000bc3, 0x00000bc4, 0x00000bc5, 0x00000bc6, 0x00000bc7, 0x00000bc8,
            0x00000bc9, 0x00000bca, 0x00000bcc, 0x00000bcd, 0x00000bce, 0x00000bcf, 0x00000bd0, 0x00000bd1,
            0x00000fa0, 0x00000fa1, 0x00000fa2, 0x00000fa3, 0x00000fa4, 0x00000fa5, 0x00000fa6, 0x00000fa7,
            0x00000fa8, 0x00000fa9, 0x00000faa, 0x00000fab, 0x00000fac, 0x00000fad, 0x00000fae, 0x00000faf,
            0x00000fb0, 0x00000fb1, 0x00019258,
            0x80002328,
    };
}
