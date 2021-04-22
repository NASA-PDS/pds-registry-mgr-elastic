package gov.nasa.pds.registry.mgr.cmd;

import org.apache.commons.cli.CommandLine;

/**
 * All Registry Manager command-line interface (CLI) commands such as 
 * "create-registry", "delete-registry", "load-data", etc.
 * should implement this interface.
 * 
 * @author karpenko
 */
public interface CliCommand
{
    /**
     * Run CLI command. 
     * @param cmdLine Command line parameters.
     * @throws Exception
     */
    public void run(CommandLine cmdLine) throws Exception;
}
