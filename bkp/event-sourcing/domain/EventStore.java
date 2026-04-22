public interface EventStore {

    List<WalletEvent> load(Long walletId);

    void append(Long walletId, List<WalletEvent> events);
}