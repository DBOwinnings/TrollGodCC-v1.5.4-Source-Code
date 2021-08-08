package me.hollow.trollgod.api.mixin.accessors;

public interface IPlayerControllerMP
{
    void setIsHittingBlock(final boolean p0);
    
    void setBlockHitDelay(final int p0);
    
    float getCurBlockDamageMP();
}
