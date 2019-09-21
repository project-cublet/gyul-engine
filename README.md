# gyul-engine

Gyul-engine is a Java based flow engine such as Node-RED.

## JSON DSL

```json
{
    "id": "f1",
    "name": "testFlow",
    "description": null,
    "enabled": true,
    "params": {},
    "nodes": [
        {
            "id": "b1",
            "name": "button",
            "description": null,
            "nodeClazz": "io.gyul.flow.node.trigger.ButtonTriggerNode",
            "config": {
                "payload": null
            },
            "x": 0,
            "y": 0
        },
        {
            "id": "d1",
            "name": "debug",
            "description": null,
            "nodeClazz": "io.gyul.flow.node.output.DebugNode",
            "config": {
                "loggingLevel": "INFO",
                "messageTemplate": "payload: {{message.payload}}"
            },
            "x": 0,
            "y": 0
        }
    ],
    "wires": [
        {
            "fromNode": "b1",
            "fromPort": null,
            "toNode": "d1"
        }
    ]
}
```

## Java DSL

```java
FlowDefinition flow = FlowDefinition.builder()
		.id("f1")
		.name("testFlow")
		.node(NodeDefinition.builder()
				.id("b1")
				.name("button")
				.nodeClazz(ButtonTriggerNode.class)
				.configSrc(ButtonTriggerNodeConfig.builder().build())
				.build())
		.node(NodeDefinition.builder()
				.id("d1")
				.name("debug")
				.nodeClazz(DebugNode.class)
				.configSrc(DebugNodeConfig.builder()
						.loggingLevel(LoggingLevel.INFO)
						.messageTemplate("payload: {{message.payload}}")
						.build())
				.build())
		.wire(Wire.of("b1", "d1"))
		.build();
```



