package com.ellisiumx.elcore.account.repository;

import com.ellisiumx.elcore.account.CoreClient;
import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class AccountRepository extends RepositoryBase {

    public AccountRepository(JavaPlugin plugin) {
        super(plugin, DBPool.Core);
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void update() {
    }

    public void updateName(String uuid, String playerName) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE accounts SET name = ? WHERE uuid = ?;");
            statement.setString(1, playerName);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int login(HashMap<String, ILoginProcessor> loginProcessors, String uuid, String name) {
        int accountId = -1;
        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.execute("SELECT id FROM accounts WHERE accounts.uuid = '" + uuid + "' LIMIT 1;");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                accountId = resultSet.getInt(1);
            }
            if (accountId == -1) {
                final List<Integer> tempList = new ArrayList<Integer>(1);
                executeInsert(ACCOUNT_LOGIN_NEW, new ResultSetCallable() {
                    @Override
                    public void processResultSet(ResultSet resultSet) throws SQLException {
                        while (resultSet.next()) {
                            tempList.add(resultSet.getInt(1));
                        }
                    }
                }, new ColumnVarChar("uuid", 100, uuid), new ColumnVarChar("name", 100, name));
                accountId = tempList.get(0);
            }
            String loginString = "UPDATE accounts SET name='" + name + "', lastLogin=now() WHERE id = '" + accountId + "';";
            for (ILoginProcessor loginProcessor : loginProcessors.values()) {
                loginString += loginProcessor.getQuery(accountId, uuid, name);
            }
            statement.execute(loginString);
			/*
			while (true)
			{
				if (statementStatus)
				{
					System.out.println("ResultSet : " + statement.getResultSet().getMetaData().getColumnCount() + " columns:");

					for (int i = 0; i < statement.getResultSet().getMetaData().getColumnCount(); i++)
					{
						System.out.println(statement.getResultSet().getMetaData().getColumnName(i + 1));
					}
				}
				else
				{
                    if (statement.getUpdateCount() == -1)
                        break;

					System.out.println("Update statement : " + statement.getUpdateCount() + " rows affected.");
				}

				statementStatus = statement.getMoreResults();
			}

			System.out.println("Done");
			*/
            statement.getUpdateCount();
            statement.getMoreResults();
            for (ILoginProcessor loginProcessor : loginProcessors.values()) {
                loginProcessor.processLoginResultSet(name, accountId, statement.getResultSet());
                statement.getMoreResults();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return accountId;
    }

    /*public CoreClient executeLogin(String uuid, String playerName) {
        CoreClient client;
        try {
            client = new CoreClient(playerName);
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT id, rank FROM accounts WHERE accounts.uuid = ? LIMIT 1;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                client.setAccountId(resultSet.getInt(1));
                client.setRank(Rank.valueOf(resultSet.getString(2)));
            }
            resultSet.close();
            if(client.getAccountId() == -1) {
                statement = connection.prepareStatement("INSERT INTO accounts (uuid, name, creationDate) values(?, ?, now());", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, uuid);
                statement.setString(2, playerName);
                statement.executeUpdate();
                resultSet = statement.getGeneratedKeys();
                if(resultSet.next()) {
                    client.setAccountId(resultSet.getInt(1));
                }
                client.setRank(Rank.ALL);
            }
            statement = connection.prepareStatement("UPDATE accounts SET name = ?, lastLogin = now() WHERE id = ?;");
            statement.setString(1, playerName);
            statement.setInt(2, client.getAccountId());
            statement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
        return client;
    }*/


}