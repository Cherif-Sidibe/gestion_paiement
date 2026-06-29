package edu.ism.badwallet.wallet;

import edu.ism.badwallet.client.web.wallet.WalletCreateMapper;
import edu.ism.badwallet.client.web.wallet.dto.WalletCreateRequestDto;
import edu.ism.badwallet.client.web.wallet.dto.WalletCreateResponseDto;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final WalletCreateMapper walletCreateMapper;

    public WalletServiceImpl(WalletRepository walletRepository, WalletCreateMapper walletCreateMapper) {
        this.walletRepository = walletRepository;
        this.walletCreateMapper = walletCreateMapper;
    }

    @Override
    @Transactional
    public WalletCreateResponseDto createWallet(WalletCreateRequestDto walletCreateRequestDto) {
        if (walletRepository.existsByCode(walletCreateRequestDto.code())) {
            throw new EntityExistsException("Wallet avec le code " + walletCreateRequestDto.code() + " existe deja");
        }
        if (walletRepository.existsByPhoneNumber(walletCreateRequestDto.phoneNumber())) {
            throw new EntityExistsException(
                    "Wallet avec le telephone " + walletCreateRequestDto.phoneNumber() + " existe deja");
        }

        Wallet walletEntity = walletCreateMapper.toEntity(walletCreateRequestDto);
        walletRepository.save(walletEntity);
        return walletCreateMapper.toDto(walletEntity);
    }
}
