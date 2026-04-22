@Component
public class WalletProjector {

    private final WalletRepository repository;

    public void project(WalletEvent event) {

        Wallet wallet = repository.findById(event.getWalletId())
                .orElse(new Wallet());

        if (event instanceof MoneyDepositedEvent e) {
            wallet.updateSnapshot(wallet.getBalanceSnapshot().add(e.getAmount()));
        }

        if (event instanceof MoneyWithdrawnEvent e) {
            wallet.updateSnapshot(wallet.getBalanceSnapshot().subtract(e.getAmount()));
        }

        repository.save(wallet);
    }
}