package online.keriils.supertravelstaff.config;

@SuppressWarnings("unused")
public final class MixinConfig {

    public static MixinConfig INSTANCE;

    public static final MixinConfig DEFAULT_CONFIG = new MixinConfig();

    public Entry<Boolean> withEnderIO = new Entry<>(true, "Enable integration with EnderIO");

    public Entry<Boolean> withTravelAnchors = new Entry<>(true, "Enable integration with Travel Anchors");

    public record Entry<T> (T value, String desc) {}
}
