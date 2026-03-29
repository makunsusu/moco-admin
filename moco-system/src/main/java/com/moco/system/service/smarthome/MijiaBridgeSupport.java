package com.moco.system.service.smarthome;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MijiaBridgeSupport
{
    @Value("${moco.smarthome.local-python:${MOCO_MIJIA_LOCAL_PYTHON:python3}}")
    private String localPython;

    @Value("${moco.smarthome.bridge-dir:${MOCO_MIJIA_BRIDGE_DIR:tools/mijia-local-bridge}}")
    private String bridgeDir;

    public String getLocalPython()
    {
        return localPython;
    }

    public String resolveScript(String scriptName)
    {
        for (Path baseDir : candidateBaseDirs())
        {
            Path scriptPath = baseDir.resolve(scriptName).normalize();
            if (Files.exists(scriptPath))
            {
                return scriptPath.toString();
            }
        }
        throw new MijiaClientException("未找到米家桥接脚本，请检查 MOCO_MIJIA_BRIDGE_DIR 配置：" + bridgeDir);
    }

    private List<Path> candidateBaseDirs()
    {
        List<Path> candidates = new ArrayList<>();
        Path configured = Paths.get(bridgeDir);
        if (configured.isAbsolute())
        {
            candidates.add(configured.normalize());
            return candidates;
        }
        addCandidateChain(candidates, Paths.get(System.getProperty("user.dir", ".")), configured);
        try
        {
            Path codeSource = Paths.get(MijiaBridgeSupport.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path origin = Files.isDirectory(codeSource) ? codeSource : codeSource.getParent();
            addCandidateChain(candidates, origin, configured);
        }
        catch (Exception ignored)
        {
        }
        return candidates;
    }

    private void addCandidateChain(List<Path> candidates, Path start, Path relative)
    {
        if (start == null)
        {
            return;
        }
        Path current = start.normalize();
        while (current != null)
        {
            Path candidate = current.resolve(relative).normalize();
            if (!candidates.contains(candidate))
            {
                candidates.add(candidate);
            }
            current = current.getParent();
        }
    }
}
