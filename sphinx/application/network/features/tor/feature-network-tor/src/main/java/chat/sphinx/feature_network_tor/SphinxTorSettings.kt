package chat.sphinx.feature_network_tor

import chat.sphinx.concept_network_tor.TorManager
import io.matthewnelson.topl_service_base.ApplicationDefaultTorSettings

internal class SphinxTorSettings: ApplicationDefaultTorSettings() {

    override val connectionPadding: String
        get() = ConnectionPadding.OFF

    override val customTorrc: String?
        get() = null

    override val disableNetwork: Boolean
        get() = DEFAULT__DISABLE_NETWORK

    override val dnsPort: String
        get() = PortOption.DISABLED

    override val dnsPortIsolationFlags: List<String>
        get() = listOf(
            IsolationFlag.ISOLATE_CLIENT_PROTOCOL
        )

    override val dormantClientTimeout: Int
        get() = DEFAULT__DORMANT_CLIENT_TIMEOUT

    override val entryNodes: String
        get() = DEFAULT__ENTRY_NODES

    override val excludeNodes: String
        get() = DEFAULT__EXCLUDED_NODES

    override val exitNodes: String
        get() = DEFAULT__EXIT_NODES

    override val hasBridges: Boolean
        get() = DEFAULT__HAS_BRIDGES

    override val hasCookieAuthentication: Boolean
        get() = DEFAULT__HAS_COOKIE_AUTHENTICATION

    override val hasDebugLogs: Boolean
        get() = DEFAULT__HAS_DEBUG_LOGS

    override val hasDormantCanceledByStartup: Boolean
        get() = DEFAULT__HAS_DORMANT_CANCELED_BY_STARTUP

    override val hasOpenProxyOnAllInterfaces: Boolean
        get() = DEFAULT__HAS_OPEN_PROXY_ON_ALL_INTERFACES

    override val hasReachableAddress: Boolean
        get() = DEFAULT__HAS_REACHABLE_ADDRESS

    override val hasReducedConnectionPadding: Boolean
        get() = DEFAULT__HAS_REDUCED_CONNECTION_PADDING

    override val hasSafeSocks: Boolean
        get() = DEFAULT__HAS_SAFE_SOCKS

    override val hasStrictNodes: Boolean
        get() = DEFAULT__HAS_STRICT_NODES

    override val hasTestSocks: Boolean
        get() = DEFAULT__HAS_TEST_SOCKS

    override val httpTunnelPort: String
        get() = PortOption.DISABLED

    override val httpTunnelPortIsolationFlags: List<String>
        get() = listOf(
            IsolationFlag.ISOLATE_CLIENT_PROTOCOL,
//            IsolationFlag.ONION_TRAFFIC_ONLY
        )

    override val isAutoMapHostsOnResolve: Boolean
        get() = DEFAULT__IS_AUTO_MAP_HOSTS_ON_RESOLVE

    override val isRelay: Boolean
        get() = DEFAULT__IS_RELAY

    override val listOfSupportedBridges: List<String>
        get() = emptyList()

    override val proxyHost: String
        get() = DEFAULT__PROXY_HOST

    override val proxyPassword: String
        get() = DEFAULT__PROXY_PASSWORD

    override val proxyPort: Int?
        get() = null

    override val proxySocks5Host: String
        get() = DEFAULT__PROXY_SOCKS5_HOST

    override val proxySocks5ServerPort: Int?
        get() = null

    override val proxyType: String
        get() = ProxyType.DISABLED

    override val proxyUser: String
        get() = DEFAULT__PROXY_USER

    override val reachableAddressPorts: String
        get() = DEFAULT__REACHABLE_ADDRESS_PORTS

    override val relayNickname: String
        get() = DEFAULT__RELAY_NICKNAME

    override val relayPort: String
        get() = PortOption.DISABLED

    override val runAsDaemon: Boolean
        get() = DEFAULT__RUN_AS_DAEMON

    override val socksPort: String
        get() = TorManager.DEFAULT_SOCKS_PORT.toString()

    override val socksPortIsolationFlags: List<String>
        get() = listOf(
            IsolationFlag.KEEP_ALIVE_ISOLATE_SOCKS_AUTH,
            IsolationFlag.ISOLATE_CLIENT_PROTOCOL,
//            IsolationFlag.ONION_TRAFFIC_ONLY,
        )

    override val transPort: String
        get() = PortOption.DISABLED

    override val transPortIsolationFlags: List<String>?
        get() = listOf(
            IsolationFlag.ISOLATE_CLIENT_PROTOCOL,
        )

    override val useSocks5: Boolean
        get() = DEFAULT__USE_SOCKS5

    override val virtualAddressNetwork: String?
        get() = "10.192.0.21/10"
}
