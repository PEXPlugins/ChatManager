# ChatManager
ChatManager, introduced in PEX 1.13, offers reliable "out of the box" functionality of a conventional chat plugin like a iChat or HeroChat.  ChatManager offers basic chat customization such as chat colours and prefixes/suffixes, as well as a per user/group message format and a ranged chat with global chat mode.

ChatManager now supports Multiverse fancy world aliases in chat!

## Configuration
**ChatManager is disabled by default**

To enable ChatManager after it is installed, you will need to edit the default config.yml in the ChatManager directory and set `enable` to `true`.

An example ChatManager config file:
<pre>enable: true
chat-range: 100.0
display-name-format: '%prefix%player%suffix'
global-message-format: <%prefix%player%suffix> &e%message
message-format: <%prefix%player%suffix> %message
ranged-mode: false</pre>

### Configuration properties
<table width="100%">
<thead><tr><th width="20%">Property</th><th>Default Value</th><th>Description</th></tr></thead>
<tbody>
<tr>
  <td>enable</td><td>false</td><td>Determines if ChatManager is enabled or not</td>
</tr>
<tr>
  <td>ranged-mode</td><td>false</td>
  <td>Enables Ranged Mode, a special mode in which chat messages have limited range.  For more details look at the <a href="#wiki-ranged-mode">Ranged Mode</a> section</td>
</tr>
<tr>
  <td>chat-range</td><td>100</td>
  <td>Amount of blocks in which a chat message can be heard.  Only used if ranged mode is enabled</td>
</tr>
<tr>
  <td>message-format</td><td><code>&lt;%prefix%player%suffix&gt; %message</code></td>
  <td>Default message format.  For placeholders look into <a href="#Message-Format-Placeholders">Message Format Placeholders</a> section.</td>
</tr>
<tr>
  <td>global-message-format</td><td><code>&lt;%prefix%player%suffix&gt; &e%message</code></td>
  <td>Message format for Global Messages.  For more details, look at the <a href="#ranged-mode">Ranged Mode</a> section.</td>
</tr>
</tbody>
</table>
**NOTE: If you remove the &lt; and &gt; from the lines for the messages, the new message format must be in quotes as YAML can not parse the % characters.  For example, `<%prefix%player%suffix> &e%message` becomes `"%prefix%player%suffix &e%message"`**

### User/Group options
It's also possible to specify user/group specific settings.  For example, for only specific groups, you can enable Ranged Mode, change their chat range, or specify their message format. They are almost identical to Configuration properties.
If not specified than values from configuration file would be used.
<table width="100%">
<thead>
<tr>
  <th>Option</th><th>Description</th>
</tr>
</thead>
<tbody>
<tr>
  <td>force-ranged-mode</td>
  <td>Enables Ranged Mode for specific user/group.</td>
</tr>
<tr>
  <td>chat-range</td>
  <td>Chat range for user/group, in blocks.</td>
</tr>
<tr>
  <td>message-format</td>
  <td>Default message format</td>
</tr>
<tr>
  <td>global-message-format</td>
  <td>Message format for global messages.</td>
</tr>
</tbody>
</table>

### Example ###
<code>/pex group Admins set message-format "&4[admin] &f%player: %message"</code>

This will set <code>message-format</code> for group __Admins__ (and subgroups) only to <code>&4[admin] &f%player: %message</code>.

### Permissions <a name="Permissions"></a> <a href="#index">(top)</a>
ChatManager also has a few permissions:
<table width="100%">
<thead>
<tr>
    <th>Permission</th><th>Description</th>
</tr>
</thead>
<tbody>
<tr>
  <td>chatmanager.chat.global</td>
  <td>Permission to send global messages when ranged mode enabled.</td>
</tr>
<tr>
  <td>chatmanager.chat.color</td>
  <td>Permission to use color codes in chat message. Example: <code>Hi &5There!</code></td>
</tr>
<tr>
  <td>chatmanager.chat.magic</td>
  <td>Permission to use the 'magic' &k color code in chat message. Example: <code>&kWTF</code></td>
</tr>
<tr>
  <td>chatmanager.chat.bold</td>
  <td>Permission to use the &l color code to embolden text. Example: <code>&lBoldly going nowhere!</code></td>
</tr>
<tr>
  <td>chatmanager.chat.strikethrough</td>
  <td>Permission to use the &m color code to strikethrough text. Example: <code>&mStrike</code></td>
</tr>
<tr>
  <td>chatmanager.chat.underline</td>
  <td>Permission to use the &n color code to underline text. Example: <code>&nThis text is underlined</code></td>
</tr>
<tr>
  <td>chatmanager.chat.italic</td>
  <td>Permission to use the &o color code to italicize text. Example: <code>&oEmphasised text</code></td>
</tr>
<tr>
  <td>chatmanager.override.ranged</td>
  <td>Permission to override/ignore the ranged chat setting.  If this is not explicitly disabled for groups that have * permissions, all chat messages will be sent globally (but will not be coloured as such)</td>
</tr>
</tbody>
</table>

## Ranged Mode
ChatManager contains a Ranged Mode feature.  When this mode is enabled, any message is limited by the specified range (default range is 100 blocks).  What this means is that a message can be seen only if a player is with the specified range of the player who typed the message (think of someone who too far away to hear someone else talking).

ChatManager also includes a Global Message feature which requires a special permission (<code>chatmanager.chat.global</code>) node to use this ability.   Global Messages are messages that are not limited by range.

To send a global message,  you have to type the exclamation "!" mark (otherwise known as a bang) **before** the message text.
Example: <code>!Hi everyone, i have good news for you!</code>.

Global Messages also have their own message format and are colured orange by default.  Check <code>global-message-format</code> configuration property for details.

## Message Format Placeholders <a name="Message-Format-Placeholders"></a> <a href="#index">(top)</a>
These are the placeholders/server variables that PEX supports and assumes are available.  If another plugin offers others, such as essentials nickname function (`%displayname`), they can be added to the config file.
<table width="100%">
<thead>
<tr>
  <th>Placeholder</th>
  <th>Description</th>
</tr>
</thead>
<tbody>
<tr>
  <td><code>%message</code></td>
  <td>Message text</td>
</tr>
<tr>
  <td><code>%player</code></td>
  <td>Sender name</td>
</tr>
<tr>
  <td><code>%prefix</code></td>
  <td>Sender prefix</td>
</tr>
<tr>
  <td><code>%suffix</code></td>
  <td>Sender suffix</td>
</tr>
<tr>
  <td><code>%world</code></td>
  <td>World the sender is in.  If Multiverse is enabled, the colored alias of the world will be used.</td>
</tr>
</tbody>
</table>

### Time placeholders
ChatManager also has time placeholders which are based on the php date() function.  Note that only the time related placeholders are implemented.
<table width="100%">
<thead>
<tr>
  <th>Code</th>
  <th>Description</th>
</tr>
</thead>
<tbody>
<tr>
  <td><code>%H</code></td>
  <td>24-hour format of an hour with leading zeros: <code>00-23</code></td>
</tr>
<tr>
  <td><code>%i</code></td>
  <td>Minutes with leading zeros: <code>00-59</code></td>
</tr>
<tr>
  <td><code>%h</code></td>
  <td>12-hour format of an hour with leading zeros: <code>01-12</code></td>
</tr>
<tr>
  <td><code>%s</code></td>
  <td>Seconds, with leading zeros: <code>00-59</code></td>
</tr>
<tr>
  <td><code>%G</code></td>
  <td>24-hour format of an hour without leading zeros: <code>0-23</code></td>
</tr>
<tr>
  <td><code>%a</code></td>
  <td>Lowercase Ante meridiem and Post meridiem: <code>am or pm</code></td>
</tr>
<tr>
  <td><code>%g</code></td>
  <td>12-hour format of an hour without leading zeros: <code>1-12</code></td>
</tr>
<tr>
  <td><code>%A</code></td>
  <td>Uppercase Ante meridiem and Post meridiem: <code>AM or PM</code></td>
</tr>
</tbody>
</table>