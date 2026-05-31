package net.hytaledepot.templates.plugin.inventory;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class InventoryDemoService {
  private final Map<String, AtomicLong> actionCounters = new ConcurrentHashMap<>();
  private final Map<String, String> lastActionBySender = new ConcurrentHashMap<>();
  private final Map<String, Map<String, Integer>> stashes = new ConcurrentHashMap<>();
  private volatile Path dataDirectory;

  public void initialize(Path dataDirectory) {
    this.dataDirectory = dataDirectory;
    stashes.clear();
  }

  public void onHeartbeat(long tick) {
    actionCounters.computeIfAbsent("heartbeat", key -> new AtomicLong()).incrementAndGet();

  }

  public void recordExternalEvent(String key) {
    actionCounters.computeIfAbsent(String.valueOf(key), item -> new AtomicLong()).incrementAndGet();
  }

  public String applyAction(InventoryPluginState state, String sender, String action, long heartbeatTicks) {
    String normalizedSender = String.valueOf(sender == null ? "unknown" : sender);
    String normalizedAction = normalizeAction(action);

    actionCounters.computeIfAbsent(normalizedAction, key -> new AtomicLong()).incrementAndGet();
    lastActionBySender.put(normalizedSender, normalizedAction);

    if ("toggle".equals(normalizedAction)) {
      boolean enabled = state.toggleDemoFlag();
      return "[Inventory] demoFlag=" + enabled + ", heartbeatTicks=" + heartbeatTicks;
    }

    if ("info".equals(normalizedAction)) {
      return "[Inventory] " + diagnostics();
    }

    String domainResult = handleDomainAction(normalizedSender, normalizedAction, heartbeatTicks);
    if (domainResult != null) {
      return "[Inventory] " + domainResult;
    }

    return "[Inventory] unknown action='" + normalizedAction + "' (try: info, toggle, sample, stash-demo, consume-demo, list-stash)";
  }

  public String describeLastAction(String sender) {
    return lastActionBySender.getOrDefault(String.valueOf(sender), "none");
  }

  public long operationCount() {
    long total = 0;
    for (AtomicLong value : actionCounters.values()) {
      total += value.get();
    }
    return total;
  }

  public String diagnostics() {
    String directory = dataDirectory == null ? "unset" : dataDirectory.toString();
    int totalStacks = stashes.values().stream().mapToInt(Map::size).sum();
    return "ops=" + operationCount()
        + ", playersWithStash=" + stashes.size()
        + ", totalStacks=" + totalStacks
        + ", dataDirectory=" + directory;
  }

  public void shutdown() {
    stashes.clear();
  }

  private String handleDomainAction(String sender, String action, long heartbeatTicks) {
    Map<String, Integer> stash = stashOf(sender);
    if ("sample".equals(action) || "stash-demo".equals(action)) {
      stash.merge("starter_wood_sword", 1, Integer::sum);
      stash.merge("healing_potion", 2, Integer::sum);
      return "stash updated " + new TreeMap<>(stash);
    }
    if ("consume-demo".equals(action)) {
      int current = stash.getOrDefault("healing_potion", 0);
      if (current <= 0) {
        return "no healing_potion available";
      }
      if (current == 1) {
        stash.remove("healing_potion");
      } else {
        stash.put("healing_potion", current - 1);
      }
      return "consumed healing_potion, stash=" + new TreeMap<>(stash);
    }
    if ("list-stash".equals(action)) {
      return "stash=" + new TreeMap<>(stash);
    }
    return null;
  }

  private Map<String, Integer> stashOf(String sender) {
    return stashes.computeIfAbsent(String.valueOf(sender).toLowerCase(), key -> new ConcurrentHashMap<>());
  }

  private static String normalizeAction(String action) {
    String normalized = String.valueOf(action == null ? "" : action).trim().toLowerCase();
    return normalized.isEmpty() ? "sample" : normalized;
  }
}
