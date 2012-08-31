#!/dev/null

if ! test "${#}" -eq 0 ; then
	echo "[ee] invalid arguments; aborting!" >&2
	exit 1
fi

if test "${_mosaic_deploy_cook:-true}" == true ; then
	ssh -T "${_package_cook}" <"${_outputs}/package.tar.gz"
fi

if test "${_mosaic_deploy_artifactory:-true}" == true ; then
	case "${_maven_pom_classifier}" in
		
		( component )
			env "${_mvn_env[@]}" "${_mvn_bin}" \
					-f "${_mvn_pom}" \
					--projects "${_maven_pom_group}:${_maven_pom_artifact}" \
					--also-make \
					"${_mvn_args[@]}" \
					deploy \
					-DskipTests=true \
					-D_maven_pom_skip_analyze=true \
					-D_maven_pom_skip_licenses=true \
					-D_maven_pom_skip_formatter=true
		;;
		
		( artifacts )
			# FIXME: We have to fix this...
			env "${_mvn_env[@]}" "${_mvn_bin}" \
					-f "${_mvn_pom}" \
					--also-make \
					"${_mvn_args[@]}" \
					deploy \
					-DskipTests=true \
					-D_maven_pom_skip_analyze=true \
					-D_maven_pom_skip_licenses=true \
					-D_maven_pom_skip_formatter=true
		;;
		
		( * )
			exit 1
		;;
	esac
fi

exit 0
