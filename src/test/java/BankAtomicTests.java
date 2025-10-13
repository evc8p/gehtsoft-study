import com.evch.rrm.BankAtomic;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankAtomicTests {
    @Test
    void getSumOfAllAccountsMethodShouldReturnSameResultBeforeAndAfterTransferOfFunds1000Times() throws InterruptedException {
        BankAtomic bank = new BankAtomic(200, 0, 1000);
        for (int i = 0; i < 1000; i++) {
            BigInteger sumBefore = bank.getSumOfAllAccounts();
            bank.startTransfers();
            BigInteger sumAfter = bank.getSumOfAllAccounts();
            assertEquals(sumBefore, sumAfter,
                    "Sum before the transfer is not equals to sum after the transfer\n on iteration = " + i);
        }
    }
}
