/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * verchor
 * Copyright (C) 2014 Pascal Poizat (@pascalpoizat)
 * emails: pascal.poizat@lip6.fr
 */

package refactoring_from_python.verification;

public abstract class Tool {
    private boolean verbose;

    public Tool() { this(false); }

    public Tool(boolean verbose) { this.verbose = verbose; }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void message(String msg) {
        if (verbose) {
            System.out.println("" + msg);
        }
    }

    public static void error(String msg) {
        System.out.println("ERROR: " + msg);
    }

    public static void warning(String msg) {
        System.out.println("WARNING: " + msg);
    }

}
