# -------------------------------------------------------------------------
# Package
#    TwitterDriver.pl
#
# Dependencies
#    none
#
# Purpose
#    Create a command line for the EC-Twitter command line utility
#
# Date
#    01/06/2012
#
# Engineer
#    Carlos Rojas
#
# Copyright (c) 2011 Electric Cloud, Inc.
# All rights reserved
# -------------------------------------------------------------------------
package TwitterDriver;


# -------------------------------------------------------------------------
# Includes
# -------------------------------------------------------------------------
use strict;
use warnings;
use ElectricCommander;
$|=1;

sub main {
    my $ec = ElectricCommander->new();
    $ec->abortOnError(0);
    
    # -------------------------------------------------------------------------
    # Parameters
    # -------------------------------------------------------------------------
    my $JavaPath        = ($ec->getProperty( "JavaPath" ))->findvalue('//value')->string_value;
    my $CommandLineTool = ($ec->getProperty( "CommandLineTool" ))->findvalue('//value')->string_value;
    my $Message         = ($ec->getProperty( "Message" ))->findvalue('//value')->string_value;
    my $ConfigFile      = ($ec->getProperty( "ConfigFile" ))->findvalue('//value')->string_value;
    
    my @cmd = ();
    my %props;
    
    if($JavaPath && $JavaPath ne ""){
        push(@cmd, qq{"$JavaPath" -jar});
    }
    
    if($CommandLineTool && $CommandLineTool ne ""){
        push(@cmd, qq{"$CommandLineTool"});
    }
    
    if($Message && $Message ne ""){
        push(@cmd, qq{"$Message"});
    }
    
    if($ConfigFile && $ConfigFile ne ""){
        push(@cmd, qq{"$ConfigFile"});
    }
    
    my $commandLine = join(" ", @cmd);
    
    $ec->setProperty("/myCall/commandLine", $commandLine);
    
   
}
  
main();
