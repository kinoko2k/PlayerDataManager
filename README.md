# PlayerDataManager
## Development
```bash
git clone https://github.com/KinokoNetWork/PlayerDataManager.git
cd PlayerDataManager
mvn clean install
```

## Maven
```yml
<dependencies>
    <dependency>
        <groupId>net.kinoko2k</groupId>
        <artifactId>PlayerDataManager</artifactId>
        <version>1.0.3</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## Depends
```yaml
depend: [PlayerDataManager]
```

## Usage
```java
import net.kinoko2k.pdm.api.PlayerDataAPI;
import net.kinoko2k.pdm.data.PlayerData;

try {
    UUID playerUUID = player.getUniqueId();
    PlayerData data = PlayerDataAPI.getPlayerData(playerUUID);
    if (data != null) {
        long playtime = data.getPlaytime();
        String lastIp = data.getLastIpAddress();
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```

### PlayTime
```java
import net.kinoko2k.pdm.api.PlayerDataAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class PlayTimeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("使用方法: /playtime <プレイヤー名>");
            return true;
        }

        String targetPlayer = args[0];

        try {
            long playtimeSeconds = PlayerDataAPI.getPlaytimeByMCID(targetPlayer);
            if (playtimeSeconds != -1) {
                sender.sendMessage(targetPlayer + "の総プレイ時間: " + playtimeSeconds + "秒");
            }

            String formattedPlaytime = PlayerDataAPI.getFormattedPlaytimeByMCID(targetPlayer);
            if (formattedPlaytime != null) {
                sender.sendMessage(targetPlayer + "の総プレイ時間: " + formattedPlaytime);
            } else {
                sender.sendMessage("プレイヤーが見つかりません。");
            }

        } catch (SQLException e) {
            sender.sendMessage("データベースエラーが発生しました。");
            e.printStackTrace();
        }

        return true;
    }
}
```