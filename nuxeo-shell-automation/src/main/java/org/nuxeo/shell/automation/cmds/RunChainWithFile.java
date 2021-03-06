/*
 * (C) Copyright 2006-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.shell.automation.cmds;

import java.io.File;

import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.model.FileBlob;
import org.nuxeo.shell.Argument;
import org.nuxeo.shell.Command;
import org.nuxeo.shell.Context;
import org.nuxeo.shell.Parameter;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.automation.ChainCompletor;
import org.nuxeo.shell.automation.DocRefCompletor;
import org.nuxeo.shell.automation.RemoteContext;
import org.nuxeo.shell.utils.StringUtils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Command(name = "runonfile", help = "Run a server automation chain that accepts a file as an input")
public class RunChainWithFile implements Runnable {

    @Context
    protected RemoteContext ctx;

    @Parameter(name = "-void", hasValue = false, help = "Use this to avoid having the server sending back the result.")
    protected boolean isVoid;

    @Parameter(name = "-ctx", hasValue = true, help = "Use this to set execution context variables. Syntax is: k1=v1,k1=v2")
    protected String ctxVars;

    @Parameter(name = "-s", hasValue = true, help = "Use this to change the separator used in context variables. The default is ','")
    protected String sep = ",";

    @Argument(name = "chain", index = 0, required = true, completor = ChainCompletor.class, help = "The chain to run")
    protected String chain;

    @Argument(name = "file", index = 1, required = true, completor = DocRefCompletor.class, help = "A reference to the new context document to use. To use UID references prefix them with 'doc:'.")
    protected File file;

    public void run() {
        try {
            OperationRequest request = ctx.getSession().newRequest(chain).setInput(new FileBlob(file));
            if (ctxVars != null) {
                for (String pair : ctxVars.split(sep)) {
                    String[] ar = StringUtils.split(pair, '=', true);
                    request.setContextProperty(ar[0], ar[1]);
                }
            }
            if (isVoid) {
                request.setHeader(Constants.HEADER_NX_VOIDOP, "true");
            }
            request.execute();

        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

}
