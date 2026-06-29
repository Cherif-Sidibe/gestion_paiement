package edu.ism.badwallet.client.web.wallet;

import edu.ism.badwallet.client.web.wallet.dto.WalletCreateRequestDto;
import edu.ism.badwallet.client.web.wallet.dto.WalletCreateResponseDto;
import edu.ism.badwallet.shared.mapper.DateMapper;
import edu.ism.badwallet.wallet.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletCreateMapper {

    private final DateMapper dateMapper;

    public WalletCreateMapper(DateMapper dateMapper) {
        this.dateMapper = dateMapper;
    }

    public Wallet toEntity(WalletCreateRequestDto walletCreateRequestDto) {
        if (walletCreateRequestDto == null) {
            return null;
        }
        return Wallet.builder()
                .phoneNumber(walletCreateRequestDto.phoneNumber())
                .email(walletCreateRequestDto.email())
                .balance(walletCreateRequestDto.initialBalance())
                .code(walletCreateRequestDto.code())
                .currency(walletCreateRequestDto.currency())
                .build();
    }

    public WalletCreateResponseDto toDto(Wallet wallet) {
        if (wallet == null) {
            return null;
        }
        return WalletCreateResponseDto.builder()
                .id(wallet.getId())
                .code(wallet.getCode())
                .phoneNumber(wallet.getPhoneNumber())
                .email(wallet.getEmail())
                .currency(wallet.getCurrency())
                .balance(wallet.getBalance())
                .createdAt(dateMapper.formatLocalDateTime(wallet.getCreatedAt(), "dd/MM/yyyy HH:mm:ss"))
                .build();
    }
}
