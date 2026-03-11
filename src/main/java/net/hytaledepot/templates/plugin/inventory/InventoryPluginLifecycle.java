package net.hytaledepot.templates.plugin.inventory;

public enum InventoryPluginLifecycle {
  NEW,
  PRELOADING,
  SETTING_UP,
  READY,
  RUNNING,
  STOPPING,
  STOPPED,
  FAILED
}
