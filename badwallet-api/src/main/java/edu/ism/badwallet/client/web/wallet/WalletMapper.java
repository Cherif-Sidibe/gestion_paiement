package edu.ism.badwallet.client.web.wallet;

import edu.ism.badwallet.client.web.wallet.dto.WalletBalanceResponseDto;
import edu.ism.badwallet.client.web.wallet.dto.WalletResponseDto;
import edu.ism.badwallet.shared.mapper.DateMapper;
import edu.ism.badwallet.wallet.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    private final DateMapper dateMapper;

    public WalletMapper(DateMapper dateMapper) {
        this.dateMapper = dateMapper;
    }

    public WalletResponseDto toDto(Wallet wallet) {
        if (wallet == null) {
            return null;
        }
        return WalletResponseDto.builder()
                .id(wallet.getId())
                .code(wallet.getCode())
                .phoneNumber(wallet.getPhoneNumber())
                .email(wallet.getEmail())
                .currency(wallet.getCurrency())
                .balance(wallet.getBalance())
                .createdAt(dateMapper.formatLocalDateTime(wallet.getCreatedAt(), "dd/MM/yyyy HH:mm:ss"))
                .build();
    }

    public WalletBalanceResponseDto toBalanceDto(Wallet wallet) {
        if (wallet == null) {
            return null;
        }
        return WalletBalanceResponseDto.builder()
                .code(wallet.getCode())
                .phoneNumber(wallet.getPhoneNumber())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .build();
    }
}
