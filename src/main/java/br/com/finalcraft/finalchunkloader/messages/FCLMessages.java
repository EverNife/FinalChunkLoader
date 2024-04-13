package br.com.finalcraft.finalchunkloader.messages;

import br.com.finalcraft.evernifecore.locale.FCLocale;
import br.com.finalcraft.evernifecore.locale.LocaleMessage;
import br.com.finalcraft.evernifecore.locale.LocaleType;

public class FCLMessages {

    @FCLocale(lang = LocaleType.PT_BR, text = "§4§l ▶ §eChunkLoader removido com sucesso!")
    @FCLocale(lang = LocaleType.EN_US, text = "§4§l ▶ §eChunkLoader successfully removed!")
    public static LocaleMessage CHUNK_LOADER_SUCCESSFULLY_REMOVED;

    @FCLocale(lang = LocaleType.PT_BR, text = "§4§l ▶ §eChunkLoader não é mais valido! Alguém quebrou o bloco?")
    @FCLocale(lang = LocaleType.EN_US, text = "§4§l ▶ §eChunkLoader is no longer valid! Did someone break the block?")
    public static LocaleMessage CHUNK_LOADER_IS_NOT_VALID;

    @FCLocale(lang = LocaleType.PT_BR, text = "§4§l ▶ §cO jogador §e%player%§c não tem chunks suficientes! §7Ele tem §2%available%§c§l/§e%need%")
    @FCLocale(lang = LocaleType.EN_US, text = "§4§l ▶ §cThe player §e%player%§c does not have enough free chunks! §7He has §2%available%§c§l/§e%need%")
    public static LocaleMessage THE_PLAYER_DOES_NOT_HAVE_ENOUGH_FREE_CHUNKS;

    @FCLocale(lang = LocaleType.PT_BR, text = "§4§l ▶ §cVocê não tem chunks suficientes! §7Você tem §2%available%§c§l/§e%need%")
    @FCLocale(lang = LocaleType.EN_US, text = "§4§l ▶ §cNot enough free chunks! §7You have §2%available%§c§l/§e%need%")
    public static LocaleMessage NOT_ENOUGH_FREE_CHUNKS;

    @FCLocale(lang = LocaleType.PT_BR, text = "§2§l ▶ §aChunkLoader atualizado com sucesso! Tamanho: %size%")
    @FCLocale(lang = LocaleType.EN_US, text = "§2§l ▶ §aChunkLoader successfully updated! Size: %size%")
    public static LocaleMessage CHUNK_LOADER_SUCCESSFULLY_UPDATED;

    @FCLocale(lang = LocaleType.PT_BR, text = "§2§l ▶ §aChunkLoader criado com sucesso! Escolha um tamanho para ele.")
    @FCLocale(lang = LocaleType.EN_US, text = "§2§l ▶ §aChunkLoader successfully created! Choose a size for it.")
    public static LocaleMessage CHUNK_LOADER_SUCCESSFULLY_CREATED;

}
