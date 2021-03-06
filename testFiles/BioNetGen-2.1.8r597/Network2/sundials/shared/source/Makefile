# -----------------------------------------------------------------
# $Revision: 1.8.2.2 $
# $Date: 2005/02/14 23:15:29 $
# -----------------------------------------------------------------
# Programmer(s): Radu Serban and Aaron Collier @ LLNL
# -----------------------------------------------------------------
# Copyright (c) 2002, The Regents of the University of California.
# Produced at the Lawrence Livermore National Laboratory.
# All rights reserved.
# For details, see sundials/shared/LICENSE.
# -----------------------------------------------------------------
# Makefile for SHARED module
#
# shared/source/Makefile.  Generated from Makefile.in by configure.
# -----------------------------------------------------------------

SHELL = /bin/sh

srcdir       = .
builddir     = .
abs_builddir = /Users/mr_smith22586/Documents/workspace/rulebender/distributionResources/BioNetGen/mac64/BioNetGen-2.1.8r597/Network2/sundials/shared/source
top_builddir = ../../
prefix       = /Users/mr_smith22586/Documents/workspace/rulebender/distributionResources/BioNetGen/mac64/BioNetGen-2.1.8r597/Network2
exec_prefix  = ${prefix}
includedir   = ${prefix}/include
libdir       = ${exec_prefix}/lib

INSTALL        = /usr/bin/install -c
INSTALL_LIB    = ${INSTALL}
INSTALL_HEADER = ${INSTALL} -m 644

LIBTOOL      = $(SHELL) $(top_builddir)/libtool
LIBTOOL_DEPS = config/ltmain.sh

CPP      = /usr/bin/cc -E
CPPFLAGS = 
CC       = /usr/bin/cc
CFLAGS   = -g -O2
LIBS     = -lm 

SHARED_INC_DIR   = $(srcdir)/../include

SHARED_LIB       = libsundials_shared.la
SHARED_LIB_FILES = band.lo dense.lo iterative.lo sundialsmath.lo nvector.lo smalldense.lo spgmr.lo

OBJECT_FILES = band.o dense.o iterative.o sundialsmath.o nvector.o smalldense.o spgmr.o

all: update $(SHARED_LIB_FILES)

update:
	${INSTALL_HEADER} ${top_builddir}/config/sundials_config.h ${builddir}

install: update $(SHARED_LIB_FILES)
	$(LIBTOOL) --mode=link $(CC) $(CFLAGS) -o $(SHARED_LIB) $(SHARED_LIB_FILES) -rpath $(libdir) $(LIBS) -avoid-version
	$(LIBTOOL) --mode=install $(INSTALL_LIB) $(SHARED_LIB) $(libdir)
	$(INSTALL_HEADER) $(SHARED_INC_DIR)/band.h $(includedir)
	$(INSTALL_HEADER) $(SHARED_INC_DIR)/dense.h $(includedir)
	$(INSTALL_HEADER) $(SHARED_INC_DIR)/iterative.h $(includedir)
	$(INSTALL_HEADER) $(SHARED_INC_DIR)/sundialsmath.h $(includedir)
	$(INSTALL_HEADER) $(SHARED_INC_DIR)/sundialstypes.h $(includedir)
	$(INSTALL_HEADER) $(SHARED_INC_DIR)/nvector.h $(includedir)
	$(INSTALL_HEADER) $(SHARED_INC_DIR)/smalldense.h $(includedir)
	$(INSTALL_HEADER) $(SHARED_INC_DIR)/spgmr.h $(includedir)
	$(INSTALL_HEADER) $(builddir)/sundials_config.h $(includedir)

clean:
	$(LIBTOOL) --mode=clean rm -f $(SHARED_LIB)
	rm -f $(SHARED_LIB_FILES)
	rm -f $(OBJECT_FILES)
	rm -f sundials_config.h

uninstall: clean
	$(LIBTOOL) --mode=uninstall rm -f $(libdir)/$(SHARED_LIB)
	rm -f $(includedir)/band.h
	rm -f $(includedir)/dense.h
	rm -f $(includedir)/iterative.h
	rm -f $(includedir)/sundialsmath.h
	rm -f $(includedir)/sundialstypes.h
	rm -f $(includedir)/nvector.h
	rm -f $(includedir)/smalldense.h
	rm -f $(includedir)/spgmr.h
	@if test -f ${includedir}/sundials_config.h ; then \
	   echo "rm -f $(includedir)/sundials_config.h" ;  \
	   rm -f $(includedir)/sundials_config.h ;         \
	 fi

band.lo: $(srcdir)/band.c
	$(LIBTOOL) --mode=compile $(CC) $(CPPFLAGS) -I. -I$(SHARED_INC_DIR) $(CFLAGS) -c $(srcdir)/band.c
dense.lo: $(srcdir)/dense.c
	$(LIBTOOL) --mode=compile $(CC) $(CPPFLAGS) -I. -I$(SHARED_INC_DIR) $(CFLAGS) -c $(srcdir)/dense.c
iterative.lo: $(srcdir)/iterative.c
	$(LIBTOOL) --mode=compile $(CC) $(CPPFLAGS) -I. -I$(SHARED_INC_DIR) $(CFLAGS) -c $(srcdir)/iterative.c
sundialsmath.lo: $(srcdir)/sundialsmath.c
	$(LIBTOOL) --mode=compile $(CC) $(CPPFLAGS) -I. -I$(SHARED_INC_DIR) $(CFLAGS) -c $(srcdir)/sundialsmath.c
sundialstypes.lo: $(srcdir)/sundialstypes.c
	$(LIBTOOL) --mode=compile $(CC) $(CPPFLAGS) -I. -I$(SHARED_INC_DIR) $(CFLAGS) -c $(srcdir)/sundialstypes.c
nvector.lo: $(srcdir)/nvector.c
	$(LIBTOOL) --mode=compile $(CC) $(CPPFLAGS) -I. -I$(SHARED_INC_DIR) $(CFLAGS) -c $(srcdir)/nvector.c
smalldense.lo: $(srcdir)/smalldense.c
	$(LIBTOOL) --mode=compile $(CC) $(CPPFLAGS) -I. -I$(SHARED_INC_DIR) $(CFLAGS) -c $(srcdir)/smalldense.c
spgmr.lo: $(srcdir)/spgmr.c
	$(LIBTOOL) --mode=compile $(CC) $(CPPFLAGS) -I. -I$(SHARED_INC_DIR) $(CFLAGS) -c $(srcdir)/spgmr.c

libtool: $(top_builddir)/$(LIBTOOL_DEPS)
	@cd $(top_builddir) ;                 \
	 $(SHELL) ./config.status --recheck ; \
	 cd $(abs_builddir)
