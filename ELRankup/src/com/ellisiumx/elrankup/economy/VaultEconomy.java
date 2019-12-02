package com.ellisiumx.elrankup.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class VaultEconomy implements Economy {

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public String getName() { return "ELRankup"; }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        return "$ " + amount;
    }

    @Override
    public String currencyNamePlural() {
        return "dolars";
    }

    @Override
    public String currencyNameSingular() {
        return "dolar";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return EconomyManager.repository.hasAccountByName(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return EconomyManager.repository.hasAccountByUUID(offlinePlayer.getUniqueId().toString());
    }

    @Override
    public boolean hasAccount(String playerName, String world) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String world) {
        return hasAccount(offlinePlayer);
    }

    @Override
    public double getBalance(String playerName) {
        Double balance = EconomyManager.repository.getBalanceByName(playerName);
        if(balance != null) return balance;
        else return -1;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        Double balance = EconomyManager.repository.getBalanceByUUID(offlinePlayer.getUniqueId().toString());
        if(balance != null) return balance;
        else return -1;
    }

    @Override
    public double getBalance(String playerName, String world) {
        Double balance = EconomyManager.repository.getBalanceByName(playerName);
        if(balance != null) return balance;
        else return -1;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String world) {
        Double balance = EconomyManager.repository.getBalanceByUUID(offlinePlayer.getUniqueId().toString());
        if(balance != null) return balance;
        else return -1;
    }

    @Override
    public boolean has(String playerName, double amount) {
        if(amount <= 0) return false;
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        if(amount <= 0) return false;
        return getBalance(offlinePlayer) >= amount;
    }

    @Override
    public boolean has(String playerName, String world, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String world, double amount) {
        return has(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        try {
            EconomyManager.repository.withdrawAmountByName(playerName, amount);
            double balance = getBalance(playerName);
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
        } catch (Exception ex) {
            return new EconomyResponse(amount, -1, EconomyResponse.ResponseType.FAILURE, ex.getMessage());
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        try {
            EconomyManager.repository.withdrawAmountByUUID(offlinePlayer.getUniqueId().toString(), amount);
            double balance = getBalance(offlinePlayer);
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
        } catch (Exception ex) {
            return new EconomyResponse(amount, -1, EconomyResponse.ResponseType.FAILURE, ex.getMessage());
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String world, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
        return withdrawPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        try {
            EconomyManager.repository.depositAmountByName(playerName, amount);
            double balance = getBalance(playerName);
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
        } catch (Exception ex) {
            return new EconomyResponse(amount, -1, EconomyResponse.ResponseType.FAILURE, ex.getMessage());
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        try {
            EconomyManager.repository.depositAmountByUUID(offlinePlayer.getUniqueId().toString(), amount);
            double balance = getBalance(offlinePlayer);
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
        } catch (Exception ex) {
            return new EconomyResponse(amount, -1, EconomyResponse.ResponseType.FAILURE, ex.getMessage());
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String world, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
        return depositPlayer(offlinePlayer, amount);
    }

    // IGNORE

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }
}
