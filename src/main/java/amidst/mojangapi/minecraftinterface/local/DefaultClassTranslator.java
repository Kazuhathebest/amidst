package amidst.mojangapi.minecraftinterface.local;

import static amidst.mojangapi.minecraftinterface.local.SymbolicNames.*;

import amidst.clazz.real.AccessFlags;
import amidst.clazz.translator.ClassTranslator;

public enum DefaultClassTranslator {
    INSTANCE;

    private final ClassTranslator classTranslator = createClassTranslator();

    public static ClassTranslator get() {
        return INSTANCE.classTranslator;
    }

    // @formatter:off
    private ClassTranslator createClassTranslator() {
        return ClassTranslator
            .builder()
                .ifDetect(c ->
                        c.getNumberOfConstructors() == 3
                        && c.getNumberOfFields() == 3
                        && c.getField(0).hasFlags(AccessFlags.PRIVATE | AccessFlags.STATIC | AccessFlags.FINAL)
                        && c.searchForUtf8EqualTo("argument.id.invalid")
                        && c.searchForUtf8EqualTo("minecraft")
                )
                .thenDeclareRequired(CLASS_REGISTRY_KEY)
                    .requiredConstructor(CONSTRUCTOR_REGISTRY_KEY).real("java.lang.String").end()
            .next()
                .ifDetect(c -> c.getNumberOfConstructors() <= 1
                    && c.getNumberOfFields() > 15
                    && c.searchForUtf8EqualTo("block")
                    && c.searchForUtf8EqualTo("potion")
                    && c.searchForUtf8EqualTo("biome")
                    && c.searchForUtf8EqualTo("item")
                )
                .thenDeclareRequired(CLASS_REGISTRY)
                    .requiredField(FIELD_REGISTRY_META_REGISTRY, "f")
                    .requiredMethod(METHOD_REGISTRY_GET_ID, "a").real("java.lang.Object").end()
                    .requiredMethod(METHOD_REGISTRY_GET_BY_KEY, "a").symbolic(CLASS_REGISTRY_KEY).end()
            .next()
                .ifDetect(c -> c.searchForUtf8EqualTo("level-seed")
                	&& c.searchForUtf8EqualTo("generator-settings")
                )
                .thenDeclareRequired(CLASS_WORLD_GEN_SETTINGS)
                	.requiredMethod(METHOD_WORLD_GEN_SETTINGS_READ, "a").real("java.util.Properties").end()
                	.requiredMethod(METHOD_WORLD_GEN_SETTINGS_OVERWORLD, "g").end()
            .next()
                .ifDetect(c -> c.getRealClassName().contains("$")
                    && c.isInterface()
                    && c.getNumberOfMethods() == 1
                    && c.hasMethodWithRealArgsReturning("int", "int", "int", null)
                    && !c.hasMethodWithRealArgsReturning("int", "int", "int", "boolean")
                )
                .thenDeclareRequired(CLASS_NOISE_BIOME_PROVIDER)
                    .requiredMethod(METHOD_NOISE_BIOME_PROVIDER_GET_BIOME, "b").real("int").real("int").real("int").end()
            .next()
                .ifDetect(c -> !c.getRealClassName().contains("$")
                    && c.getRealSuperClassName().equals("java/lang/Enum")
                    && c.hasMethodWithRealArgsReturning("long", "int", "int", "int", null, null)
                    && c.getNumberOfMethods() == 4
                )
                .thenDeclareRequired(CLASS_OVERWORLD_BIOME_ZOOMER)
                    .requiredMethod(METHOD_BIOME_ZOOMER_GET_BIOME, "a").real("long").real("int").real("int").real("int").symbolic(CLASS_NOISE_BIOME_PROVIDER).end()
            .next()
                .ifDetect(c ->
                    c.getNumberOfConstructors() == 1
                    && c.getNumberOfFields() > 0
                    && c.getField(0).hasFlags(AccessFlags.STATIC | AccessFlags.FINAL)
                    && (c.searchForFloat(0.62222224F) || c.searchForUtf8EqualTo("Feature placement"))
                )
                .thenDeclareRequired(CLASS_BIOME)
            .next()
                .ifDetect(c ->
                    (c.searchForStringContaining("Server-Worker-")
                     || c.searchForStringContaining("Worker-"))
                    && c.searchForStringContaining("os.name")
                    && c.searchForLong(1000000L)
                )
                .thenDeclareOptional(CLASS_UTIL)
                    .optionalField(FIELD_UTIL_SERVER_EXECUTOR, "c")
            .construct();
    }
    // @formatter:on
}
