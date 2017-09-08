package org.jcodec.codecs.vpx.vp9;

import org.jcodec.common.ArrayUtil;
import org.junit.Assert;
import org.junit.Test;

public class InterModeInfoTest {
    private static int[] Y_MODE_PROBS = { 35, 32, 18, 144, 218, 194, 41, 51, 98, 44, 68, 18, 165, 217, 196, 45, 40, 78,
            173, 80, 19, 176, 240, 193, 64, 35, 46, 221, 135, 38, 194, 248, 121, 96, 85, 29 };

    private static int[] INTER_MODE_PROBS = { 2, 173, 34, 7, 145, 85, 7, 166, 63, 7, 94, 66, 8, 64, 46, 17, 81, 31, 25,
            29, 30 };
    
    private static int[] INTERP_FILTER_PROBS = { 2, 173, 34, 7, 145, 85, 7, 166, 63, 7, 94, 66, 8, 64, 46, 17, 81, 31, 25,
            29, 30 };

    @Test
    public void testReadInterModeInfo() {
        MockVPXBooleanDecoder decoder = new MockVPXBooleanDecoder(new int[] {}, new int[] {});
        DecodingContext c = new DecodingContext();
        int miCol = 0;
        int miRow = 0;
        int blSz = 0;
        c.miTileStartCol = 0;

        InterModeInfo modeInfo = InterModeInfo.read(miCol, miRow, blSz, decoder, c);

        Assert.assertEquals(true, modeInfo.isInter());
        Assert.assertEquals(MVList.create(MV.create(0, 0, 0), MV.create(0, 0, 0)), modeInfo.getMvl0());
        Assert.assertEquals(MVList.create(MV.create(0, 0, 0), MV.create(0, 0, 0)), modeInfo.getMvl1());
        Assert.assertEquals(MVList.create(MV.create(0, 0, 0), MV.create(0, 0, 0)), modeInfo.getMvl2());
        Assert.assertEquals(MVList.create(MV.create(0, 0, 0), MV.create(0, 0, 0)), modeInfo.getMvl3());

        Assert.assertEquals(0, modeInfo.getSegmentId());
        Assert.assertEquals(false, modeInfo.isSkip());
        Assert.assertEquals(0, modeInfo.getTxSize());
        Assert.assertEquals(0, modeInfo.getYMode());
        Assert.assertEquals(0, modeInfo.getSubModes());
        Assert.assertEquals(0, modeInfo.getUvMode());
    }

    @Test
    public void testReadIntraMode() {
        MockVPXBooleanDecoder decoder = new MockVPXBooleanDecoder(new int[] { 44, 68, 18, 165, 217 },
                new int[] { 1, 1, 1, 0, 0 });
        DecodingContext c = new DecodingContext();
        int miCol = 9;
        int miRow = 1;
        int blSz = 3;
        ArrayUtil.fill2D(c.yModeProbs, Y_MODE_PROBS, 0);

        Assert.assertEquals(2, InterModeInfo.readInterIntraMode(miCol, miRow, blSz, decoder, c));
    }

    @Test
    public void testReadIntraModeSub() {
        MockVPXBooleanDecoder decoder = new MockVPXBooleanDecoder(
                new int[] { 35, 32, 18, 144, 41, 51, 98, 35, 32, 35, 32, 18, 144, 218, 35, 32, 101, 21, 107, 181, 192 },
                new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0 });
        DecodingContext c = new DecodingContext();
        int miCol = 8;
        int miRow = 1;
        int blSz = 0;
        ArrayUtil.fill2D(c.yModeProbs, Y_MODE_PROBS, 0);

        Assert.assertEquals(ModeInfo.vect4(7, 9, 2, 9),
                InterModeInfo.readInterIntraModeSub(miCol, miRow, blSz, decoder, c));
    }

    @Test
    public void testReadInterModeSub() {
        MockVPXBooleanDecoder decoder = new MockVPXBooleanDecoder(new int[] { 7, 94, 7, 94, 66, 7, 94, 66, 7, 94 },
                new int[] { 1, 0, 1, 1, 0, 1, 1, 0, 1, 0 });
        DecodingContext c = new DecodingContext();
        int miCol = 3;
        int miRow = 1;
        int blSz = 0;
        c.miTileStartCol = 0;
        ArrayUtil.fill2D(c.interModeProbs, INTER_MODE_PROBS, 0);

        c.aboveModes = new int[] { 0, 0, 10, 13, 0, 2, 2 };
        c.leftModes = new int[] { 10, 11, 9, 2, 2 };
        c.tileHeight = 36;
        c.tileWidth = 64;

        Assert.assertEquals(10, InterModeInfo.readLumaMode(miCol, miRow, blSz, decoder, c));
    }

    @Test
    public void testReadInterMode() {
        MockVPXBooleanDecoder decoder = new MockVPXBooleanDecoder(new int[] { 7, 166, 63 }, new int[] { 1, 1, 1 });
        DecodingContext c = new DecodingContext();
        int miCol = 0;
        int miRow = 0;
        int blSz = 0;
        c.miTileStartCol = 0;
        ArrayUtil.fill2D(c.interModeProbs, INTER_MODE_PROBS, 0);

        c.aboveModes = new int[8];
        c.leftModes = new int[8];
        c.tileHeight = 36;
        c.tileWidth = 64;

        Assert.assertEquals(13, InterModeInfo.readLumaMode(miCol, miRow, blSz, decoder, c));
    }
    
    @Test
    public void readInterpFilter() {
        int miCol = 0;
        int miRow = 0;
        int blSz = 0;
        MockVPXBooleanDecoder decoder = new MockVPXBooleanDecoder(new int[] {  }, new int[] {  });
        DecodingContext c = new DecodingContext();
        c.miTileStartCol = 0;
        c.aboveRefs = new int[] {};
        c.leftRefs = new int[] {};
        c.leftInterpFilters = new int[] {};
        c.aboveInterpFilters = new int[] {};
        ArrayUtil.fill2D(c.interpFilterProbs, INTERP_FILTER_PROBS, 0);
        
        int ret = InterModeInfo.readInterpFilter(miCol, miRow, blSz, decoder, c);
        
        Assert.assertEquals(0, ret);
    }
}
